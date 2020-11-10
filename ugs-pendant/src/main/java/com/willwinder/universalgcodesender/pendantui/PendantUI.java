/*
    Copyright 2016-2020 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.universalgcodesender.pendantui;

import com.willwinder.universalgcodesender.model.BackendAPI;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.ByteArrayOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class will launch a local webserver which will provide a simple pendant interface
 *
 * @author bobj
 */
public class PendantUI {
    private BackendAPI mainWindow;
    private Server server = null;
    private int port = 8080;

    public PendantUI(BackendAPI mainWindow) {
        this.mainWindow = mainWindow;
        BackendAPIFactory.getInstance().register(mainWindow);
    }

    public Resource getBaseResource(String directory) {
        try {
            URL res = getClass().getResource(directory);
            return Resource.newResource(res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initSwaggerInfo() {
        OpenAPI oas = new OpenAPI();
        Info info = new Info()
                .title("Universal G-code Sender API")
                .description("This is a API for controlling Universal Gcode Sender through HTTP/RPC calls.")
                .license(new License()
                        .name("GNU General Public License v3.0")
                        .url("https://www.gnu.org/licenses/gpl-3.0.html"));

        oas.info(info);
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourcePackages(Stream.of("com.willwinder.universalgcodesender").collect(Collectors.toSet()));

        try {
            new JaxrsOpenApiContextBuilder<>()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true);
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * Launches the local web server.
     *
     * @return the url for the pendant interface
     */
    public List<PendantURLBean> start() {
        try {
            initSwaggerInfo();
            server = new Server(port);

            ResourceHandler staticResourceHandler = getPendantResourceHandler();
            ContextHandler swaggerStaticResourceHandlerContext = getSwaggerContextResourceHandler();
            ServletContextHandler servletContextHandler = getApiResourceHandler();

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{servletContextHandler, staticResourceHandler, swaggerStaticResourceHandlerContext, new DefaultHandler()});
            server.setHandler(handlers);
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return getUrlList();
    }

    private ServletContextHandler getApiResourceHandler() {
        // Create a servlet servletContextHandler
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/api");
        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitOrder(1);
        servletHolder.setInitParameter("javax.ws.rs.Application", AppConfig.class.getCanonicalName());
        return servletContextHandler;
    }

    private ResourceHandler getPendantResourceHandler() {
        ResourceHandler staticResourceHandler = new ResourceHandler();
        staticResourceHandler.setDirectoriesListed(true);
        staticResourceHandler.setWelcomeFiles(new String[]{"index.html"});
        staticResourceHandler.setBaseResource(getBaseResource("/resources/ugs-pendant"));

        ContextHandler staticResourceHandlerContext = new ContextHandler();
        staticResourceHandlerContext.setContextPath("/");
        staticResourceHandlerContext.setHandler(staticResourceHandler);
        return staticResourceHandler;
    }

    private ContextHandler getSwaggerContextResourceHandler() throws URISyntaxException {
        ResourceHandler swaggerResourceHandler = new ResourceHandler();
        swaggerResourceHandler.setDirectoriesListed(true);
        swaggerResourceHandler.setWelcomeFiles(new String[]{"index.html"});

        swaggerResourceHandler
                .setResourceBase(Objects.requireNonNull(PendantUI.class.getClassLoader()
                        .getResource("META-INF/resources/webjars/swagger-ui/3.35.2"))
                        .toURI().toString());


        ResourceHandler docsResourceHandler = new ResourceHandler();
        docsResourceHandler.setResourceBase(Objects.requireNonNull(PendantUI.class.getClassLoader()
                .getResource("docs"))
                .toURI().toString());


        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{docsResourceHandler, swaggerResourceHandler});

        ContextHandler swaggerStaticResourceHandlerContext = new ContextHandler();
        swaggerStaticResourceHandlerContext.setContextPath("/docs");
        swaggerStaticResourceHandlerContext.setHandler(handlers);
        return swaggerStaticResourceHandlerContext;
    }

    /**
     * Unfortunately, this is not as simple as it seems... since you can have multiple addresses and some of those may not be available via wireless
     *
     * @return
     */
    public List<PendantURLBean> getUrlList() {
        List<PendantURLBean> out = new ArrayList<>();

        Enumeration<NetworkInterface> networkInterfaceEnum;
        try {
            networkInterfaceEnum = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (networkInterfaceEnum.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnum.nextElement();

            Enumeration<InetAddress> addressEnum = networkInterface.getInetAddresses();
            while (addressEnum.hasMoreElements()) {
                InetAddress addr = addressEnum.nextElement();
                String hostAddress = addr.getHostAddress();
                if (!hostAddress.contains(":") &&
                        !hostAddress.equals("127.0.0.1")) {
                    String url = "http://" + hostAddress + ":" + port;
                    ByteArrayOutputStream bout = QRCode.from(url).to(ImageType.PNG).stream();
                    out.add(new PendantURLBean(url, bout.toByteArray()));
                    System.out.println("Listening on: " + url);
                }
            }
        }

        return out;
    }

    public void stop() {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public BackendAPI getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(BackendAPI mainWindow) {
        this.mainWindow = mainWindow;
    }
}
