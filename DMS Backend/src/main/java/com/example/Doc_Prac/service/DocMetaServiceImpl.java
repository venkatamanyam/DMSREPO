package com.example.Doc_Prac.service;

import com.example.Doc_Prac.model.DocMeta;
import com.example.Doc_Prac.repo.DocMetaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DocMetaServiceImpl implements DocMetaService{
    private static final String FIXED_DOCId="DOC-";
    private static final String UPLOAD_DIRECTORY="D:\\Java\\File Upload\\DMS Data\\";

    @Autowired
    DocMetaRepo docMetaRepo;


    /* First add data except the document */

    @Override
    public DocMeta addData(DocMeta docMeta) {

        // return the whole object
        return docMetaRepo.save(docMeta);

    }



    /*
    Add file using PutMapping and update a row in database with full information
    */

    @Override
    public String addFile(String ticketID, MultipartFile multipartFile) throws IOException {

        // fetch the data from database using primary key (id variable)
        Optional<DocMeta> docMeta = docMetaRepo.findByTicketID(ticketID);

        // to get the document id
        String docId = generateDocID();


        // check whether docMeta variable is not null
        if(docMeta.isPresent()){

            boolean comp = false;
            boolean encryption=false;
            String docTags = "AADHAAR-CARD";

            // check whether file is compressed or not
            if(Objects.requireNonNull(multipartFile.getContentType()).equalsIgnoreCase("application/zip")){
                comp=true;
            }

            // to get the document type
            String type = multipartFile.getContentType();
            String[] docType = type.split("/");

            // to generate unique fileName
            String filename = multipartFile.getOriginalFilename();
            String uniqueFileName = docMeta.get().getTicketID()+"-"+filename;

            // split the user organization for creating directory
            String[] splitUserOrg = docMeta.get().getUserOrg().split("\\.");

            // split the user location for creating directory
            String[] splitUserLoc = docMeta.get().getLocation().split("\\.");

            // to create the path and directories
            Path path = Paths.get(UPLOAD_DIRECTORY+ File.separator + splitUserOrg[0] +File.separator+ splitUserLocation(splitUserLoc) + splitUserOrg(splitUserOrg));
            Files.createDirectories(path);

            // generate filepath
            Path filePath = path.resolve(uniqueFileName);

            // to get the relative path
            Path basePath = Paths.get("D:\\");
            Path relPath = basePath.relativize(filePath);
            String relPathWithDoubleBackSlashes = relPath.toString().replace("\\","\\\\");


            // check for the override of file or update the existing file
            DocMeta data = docMetaRepo.findByTicketID(ticketID).get();
                if(data.getDocTitle()!=null){

                        int identity = data.getId();

                        // delete the file from directory
                        Path delPath = Paths.get("D:\\\\"+ data.getDocPath().replace("\\","\\\\"));
                        File delFile = delPath.toFile();
                        delFile.delete();


//                        deleteData(identity);
                        return updateFile(identity,multipartFile);
                }

            // transfer the actual file in the directory
            try(InputStream inputStream = multipartFile.getInputStream()){
                Files.copy(inputStream,filePath);
            }

            // save data in repository
            docMetaRepo.save(DocMeta.builder()
                    .id(docMeta.get().getId())
                    .docID(docId)
                    .ticketID(ticketID)
                    .docTitle(uniqueFileName)
                    .docType(docType[1])
                    .docPath(relPathWithDoubleBackSlashes)
                    .compressed(comp)
                    .encryption(encryption)
                    .userID(docMeta.get().getUserID())
                    .userGroup(docMeta.get().getUserGroup())
                    .userOrg(docMeta.get().getUserOrg())
                    .docTags(docTags)
                    .location(docMeta.get().getLocation())
                    .createdDate(docMeta.get().getCreatedDate())
                    .lastUpdatedDate(docMeta.get().getLastUpdatedDate())
                    .build());

            return "File Upload Successfully";

        }
        else{
            return "Check the file Again!!";
        }

    }

    private String splitUserOrg(String[] splitUserOrg) {
        int len = splitUserOrg.length;
        StringBuilder directoryPath = new StringBuilder();
        for(int i=1;i<len;i++){
            directoryPath.append(splitUserOrg[i]).append(File.separator);
        }
        return directoryPath.toString();
    }

    private String splitUserLocation(String[] splitUserLoc) {
        StringBuilder directoryPath = new StringBuilder();
        for (String s : splitUserLoc) {
            directoryPath.append(s).append(File.separator);
        }
        return directoryPath.toString();
    }






     /* auto generate document ID
     @Query annotation is used in repository for taking maximum docId from database and if not available,
     it uses default value and then increase according to that */

    @Override
    public String generateDocID() {
        String docId = docMetaRepo.findLastDocID();
        int varId = (docId!=null)?Integer.parseInt(docId.substring(FIXED_DOCId.length()))+1:1011001;
        return FIXED_DOCId+varId;
    }





    /* delete function for checking functionality that if something mismatch then, it will delete data
    which first entered by post API method */

    @Override
    public String deleteData(int id) {
        docMetaRepo.deleteById(id);
        return "File or Format Not Allowed";
    }





    /*
    this method is used to override the existing file for same user only
     */
    @Override
    public String updateFile(int id, MultipartFile multipartFile) throws IOException {

        // store all data in a variable
        Optional<DocMeta> docMeta = docMetaRepo.findById(id);

        boolean comp = false;
        boolean encryption=false;
        String docTags = "AADHAAR-CARD";

        // to check whether file is compressed or not
        if(Objects.requireNonNull(multipartFile.getContentType()).equalsIgnoreCase("application/zip")){
            comp=true;
        }

        String type = multipartFile.getContentType();
        String[] docType = type.split("/");

        // split the user organization for creating directory
        String[] splitUserOrg = docMeta.get().getUserOrg().split("\\.");

        // split the user location for creating directory
        String[] splitUserLoc = docMeta.get().getLocation().split("\\.");


        String newUniqueFileName = docMeta.get().getTicketID()+"-"+multipartFile.getOriginalFilename();

        // get the path
        Path path = Paths.get(UPLOAD_DIRECTORY+ File.separator + splitUserOrg[0] +File.separator+ splitUserLocation(splitUserLoc) + splitUserOrg(splitUserOrg));
        Files.createDirectories(path);
        Path filePath = path.resolve(newUniqueFileName);

        // to get the relative path
        Path basePath = Paths.get("D:\\");
        Path relPath = basePath.relativize(filePath);
        String relPathWithDoubleBackSlashes = relPath.toString().replace("\\","\\\\");

        try(InputStream inputStream = multipartFile.getInputStream()){
            Files.copy(inputStream, filePath);
        }

        docMetaRepo.save(DocMeta.builder()
                .id(docMeta.get().getId())
                .docID(docMeta.get().getDocID())
                .ticketID(docMeta.get().getTicketID())
                .docTitle(newUniqueFileName)
                .docType(docType[1])
                .docPath(relPathWithDoubleBackSlashes)
                .compressed(comp)
                .encryption(encryption)
                .userID(docMeta.get().getUserID())
                .userGroup(docMeta.get().getUserGroup())
                .userOrg(docMeta.get().getUserOrg())
                .docTags(docTags)
                .location(docMeta.get().getLocation())
                .createdDate(docMeta.get().getCreatedDate())
                .build());

        return "File Updated Successfully";
    }


    @Override
    public byte[] openFile(String docTitle) throws IOException {
        Optional<DocMeta> docMeta = docMetaRepo.findByDocTitle(docTitle);

        if(docMeta.isPresent()){
            String docPath = "D:\\"+docMeta.get().getDocPath();

            Path fileDir = Paths.get(docPath);

            return Files.readAllBytes(new File(fileDir.toString().replace("\\","\\\\")).toPath());
        }
        else{
            return "File Not Found".getBytes();
        }
    }




    @Override
    public List<DocMeta> getParticularDeptDetails(String userGroup) {
        Optional<List<DocMeta>> docMeta = docMetaRepo.findByUserGroup(userGroup);
        return docMeta.orElse(null);
    }



    @Override
    public List<String> getDocTitles(String userId) {
        return docMetaRepo.findByUserID(userId);
    }



    @Override
    public String deleteFile(String docTitle) {
        Optional<DocMeta> docMeta = docMetaRepo.findByDocTitle(docTitle);
        if(docMeta.isPresent()){
            Path path = Paths.get("D:\\\\"+ docMeta.get().getDocPath().replace("\\","\\\\"));
            File delFile = path.toFile();
            delFile.delete();

            docMetaRepo.deleteById(docMeta.get().getId());
            return "File Deleted Successfully";
        }
        return "Please Try Again Later!!!!!!";
    }

    @Override
    public List<DocMeta> getAllDocsByDocTags(String docTags) {
        return docMetaRepo.findAllDocumentsByDocTags(docTags);
    }

}
