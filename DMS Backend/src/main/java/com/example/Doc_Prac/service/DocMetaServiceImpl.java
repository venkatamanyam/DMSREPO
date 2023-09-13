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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DocMetaServiceImpl implements DocMetaService{
    private static final String FIXED_DOCId="DOC-";
    private static final String UPLOAD_DIRECTORY="D:\\\\Java\\\\File Upload\\\\DMS Data\\";

    @Autowired
    DocMetaRepo docMetaRepo;


    /* First add data except the document */

    @Override
    public DocMeta addData(DocMeta docMeta) {

        //create date object
        LocalDate currDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String todayDate = currDate.format(dateTimeFormatter);

        // set date to DocMeta object
        docMeta.setCreatedDate(todayDate);
        docMeta.setLastUpdatedDate(todayDate);

        // save data in database and store it in a variable for returning primary key
        DocMeta data = docMetaRepo.save(docMeta);

        // return the primary key
        return data;

    }






    /*
    Add file using PutMapping and update a row in database with full information
    */

    @Override
    public String addFile(int id, MultipartFile multipartFile) throws IOException {

        // fetch the data from database using primary key (id variable)
        Optional<DocMeta> docMeta = docMetaRepo.findById(id);

        // to get the document id
        String docId = generateDocID();


        // check whether docMeta variable is not null
        if(docMeta.isPresent()){

            boolean comp = false;

            // check whether file is compressed or not
            if(Objects.requireNonNull(multipartFile.getContentType().equalsIgnoreCase("application/zip"))){
                comp=true;
            }

            // to get the document type
            String type = multipartFile.getContentType();
            String[] docType = type.split("/");

            // to generate unique fileName
            String filename = multipartFile.getOriginalFilename();
            String uniqueFileName = docMeta.get().getUserID()+"-"+filename;

            // split the filename to get filename without extension
            String[] splitFileName = uniqueFileName.split("\\.");

            // to create the path and directories
            Path path = Paths.get(UPLOAD_DIRECTORY+ File.separator + docMeta.get().getUserGroup());
            Files.createDirectories(path);

            // generate filepath
            Path filePath = path.resolve(uniqueFileName);

            // to get the relative path
            Path basePath = Paths.get("D:\\");
            Path relPath = basePath.relativize(filePath);
            String relPathWithDoubleBackSlashes = relPath.toString().replace("\\","\\\\");


            // check for the override of file or update the existing file
            List<String> documentTitles = docMetaRepo.findByUserID(docMeta.get().getUserID());
            for(String name:documentTitles){
                if(name!=null){
                    String[] splitName = name.split("\\.");
                    if(splitName[0].equalsIgnoreCase(splitFileName[0])){
                        Optional<DocMeta> docMeta1 = docMetaRepo.findByDocTitle(name);
                        int identity = docMeta1.get().getId();

                        // delete the file from directory
                        Path delPath = Paths.get("D:\\\\"+docMeta1.get().getDocPath().toString().replace("\\","\\\\"));
                        File delFile = delPath.toFile();
                        delFile.delete();


                        deleteData(id);
                        return updateFile(identity,multipartFile);
                    }
                }
            }

            // transfer the actual file in the directory
            try(InputStream inputStream = multipartFile.getInputStream()){
                Files.copy(inputStream,filePath);
            }

            // save data in repository
            docMetaRepo.save(DocMeta.builder()
                    .id(id)
                    .docID(docId)
                    .docTitle(uniqueFileName)
                    .docType(docType[1])
                    .docPath(relPathWithDoubleBackSlashes)
                    .compressed(comp)
                    .userID(docMeta.get().getUserID())
                    .userGroup(docMeta.get().getUserGroup())
                    .userOrg(docMeta.get().getUserOrg())
                    .docTags(docMeta.get().getDocTags())
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

        // set the updated date
        LocalDate currDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String updatedDate = currDate.format(dateTimeFormatter);

        boolean comp = false;

        // to check whether file is compressed or not
        if(Objects.requireNonNull(multipartFile.getContentType().equalsIgnoreCase("application/zip"))){
            comp=true;
        }

        String type = multipartFile.getContentType();
        String[] docType = type.split("/");

        // store all data in a variable
        Optional<DocMeta> docMeta = docMetaRepo.findById(id);

        String newUniqueFileName = docMeta.get().getUserID()+"-"+multipartFile.getOriginalFilename();

        // get the path
        Path path = Paths.get(UPLOAD_DIRECTORY+File.separator+docMeta.get().getUserGroup());
        Path filePath = path.resolve(newUniqueFileName);

        // to get the relative path
        Path basePath = Paths.get("D:\\");
        Path relPath = basePath.relativize(filePath);
        String relPathWithDoubleBackSlashes = relPath.toString().replace("\\","\\\\");

        try(InputStream inputStream = multipartFile.getInputStream()){
            Files.copy(inputStream, filePath);
        }

        docMetaRepo.save(DocMeta.builder()
                .id(id)
                .docID(docMeta.get().getDocID())
                .docTitle(newUniqueFileName)
                .docType(docType[1])
                .docPath(relPathWithDoubleBackSlashes)
                .compressed(comp)
                .userID(docMeta.get().getUserID())
                .userGroup(docMeta.get().getUserGroup())
                .userOrg(docMeta.get().getUserOrg())
                .docTags(docMeta.get().getDocTags())
                .location(docMeta.get().getLocation())
                .createdDate(docMeta.get().getCreatedDate())
                .lastUpdatedDate(updatedDate)
                .build());

        return "File Updated Successfully";
    }




    @Override
    public List<String> searchDocuments(String docTags) {
        return docMetaRepo.findByDocTags(docTags);
    }




    @Override
    public byte[] openFile(String docTitle) throws IOException {
        Optional<DocMeta> docMeta = docMetaRepo.findByDocTitle(docTitle);

        if(docMeta.isPresent()){
            String docPath = "D:\\\\"+docMeta.get().getDocPath();

            Path fileDIr = Paths.get(docPath);

            byte[] file = Files.readAllBytes(new File(fileDIr.toString().replace("\\","\\\\")).toPath());
            return file;
        }
        else{
            return "File Not Found".getBytes();
        }
    }




    @Override
    public List<DocMeta> getParticularDeptDetails(String userGroup) {
        Optional<List<DocMeta>> docMeta = docMetaRepo.findByUserGroup(userGroup);
        if(docMeta.isPresent()){
            return docMeta.get();
        }
        return null;
    }



    @Override
    public List<String> getDocTitles(String userId) {
        return docMetaRepo.findByUserID(userId);
    }



    @Override
    public String deleteFile(String docTitle) {
        Optional<DocMeta> docMeta = docMetaRepo.findByDocTitle(docTitle);
        if(docMeta.isPresent()){
            Path path = Paths.get("D:\\\\"+docMeta.get().getDocPath().toString().replace("\\","\\\\"));
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
