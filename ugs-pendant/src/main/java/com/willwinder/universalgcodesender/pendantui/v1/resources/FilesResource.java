package com.willwinder.universalgcodesender.pendantui.v1.resources;

import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.pendantui.v1.model.WorkspaceFileList;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Path("/v1/files")
public class FilesResource {

    @Inject
    private BackendAPI backendAPI;

    @POST
    @Path("uploadAndOpen")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Loads the specified file", tags = "Files")
    public void open(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataBodyPart bodyPart) throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = bodyPart.getContentDisposition().getFileName();
        File file = new File(tempDir + File.separator + fileName);
        Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(fileInputStream);
        backendAPI.setGcodeFile(file);
    }

    @POST
    @Path("send")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Sends a loaded file", tags = "Files")
    public void send() throws Exception {
        if (backendAPI.isPaused()) {
            backendAPI.pauseResume();
        } else {
            backendAPI.send();
        }
    }

    @GET
    @Path("pause")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Pause the file transfer", tags = "Files")
    public void pause() throws Exception {
        if (!backendAPI.isPaused()) {
            backendAPI.pauseResume();
        }
    }

    @GET
    @Path("cancel")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cancel the file transfer", tags = "Files")
    public void cancel() throws Exception {
        backendAPI.cancel();
    }

    @GET
    @Path("getWorkspaceFileList")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a list of files from the workspace directory", tags = "Files")
    public WorkspaceFileList getWorkspaceFileList() {
        List<String> workspaceFileList = backendAPI.getWorkspaceFileList();
        WorkspaceFileList result = new WorkspaceFileList();
        result.setFileList(workspaceFileList);
        return result;
    }

    @POST
    @Path("openWorkspaceFile")
    @Operation(summary = "Opens a workspace file", tags = "Files")
    public void openWorkspaceFile(@QueryParam("file") String file) throws Exception {
        backendAPI.openWorkspaceFile(file);
    }
}
