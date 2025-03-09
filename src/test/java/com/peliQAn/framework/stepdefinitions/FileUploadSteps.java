package com.peliQAn.framework.stepdefinitions;

import com.peliQAn.framework.pages.basic.FormsPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Step definitions for file upload and download functionality
 */
@Slf4j
public class FileUploadSteps {

    private FormsPage formsPage;
    private File uploadedFile;
    private File downloadedFile;
    
    public FileUploadSteps() {
        formsPage = new FormsPage();
    }
    
    @When("I select a file to upload")
    public void iSelectAFileToUpload() throws IOException {
        // Create a temporary file to upload
        uploadedFile = File.createTempFile("test-upload-", ".txt");
        FileUtils.writeStringToFile(uploadedFile, "This is test file content for upload testing", StandardCharsets.UTF_8);
        
        formsPage.selectFileForUpload(uploadedFile.getAbsolutePath());
        log.info("Selected file for upload: {}", uploadedFile.getAbsolutePath());
    }
    
    @And("I submit the file upload form")
    public void iSubmitTheFileUploadForm() {
        formsPage.submitFileUploadForm();
        log.info("Submitted file upload form");
    }
    
    @Then("I should see a success message with the file name")
    public void iShouldSeeASuccessMessageWithTheFileName() {
        String successMessage = formsPage.getFileUploadSuccessMessage();
        Assert.assertTrue(successMessage.contains("successfully uploaded"),
                "Expected success message but got: " + successMessage);
        Assert.assertTrue(successMessage.contains(uploadedFile.getName()),
                "Expected success message to contain file name " + uploadedFile.getName() + 
                " but got: " + successMessage);
        log.info("File upload success message verified: {}", successMessage);
    }
    
    @When("I download the uploaded file")
    public void iDownloadTheUploadedFile() throws IOException {
        // Download the file
        String downloadUrl = formsPage.getDownloadLinkUrl();
        downloadedFile = formsPage.downloadFile(downloadUrl);
        
        Assert.assertNotNull(downloadedFile, "Downloaded file should not be null");
        Assert.assertTrue(downloadedFile.exists(), "Downloaded file should exist");
        log.info("Downloaded file to: {}", downloadedFile.getAbsolutePath());
    }
    
    @Then("the downloaded file should match the original file")
    public void theDownloadedFileShouldMatchTheOriginalFile() throws IOException {
        String originalContent = FileUtils.readFileToString(uploadedFile, StandardCharsets.UTF_8);
        String downloadedContent = FileUtils.readFileToString(downloadedFile, StandardCharsets.UTF_8);
        
        Assert.assertEquals(downloadedContent, originalContent,
                "Downloaded file content should match original file content");
        log.info("Downloaded file content matches original file content");
        
        // Clean up
        uploadedFile.delete();
        downloadedFile.delete();
    }
}