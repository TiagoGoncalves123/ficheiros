package com.example.uploadingfiles;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    //Procura a lista dos ficheiros que foram "carregados" do StorageService e coloca-os no template do Thymeleaf. 
    //Ele calcula um link para o recurso real usando MvcUriComponentsBuilder
    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        User user = RandomUser.getRandomUser();
        if (user == null) {
            model.addAttribute("statusCode", "Erro de ligação");
            return "error";
        }
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("PhotoURL", user.getPhotoURL());

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));
        return "uploadForm";
    }

    public byte[] fileConvertToBytes(@PathVariable String filename) throws IOException {
        byte[] arr = Files.readAllBytes(storageService.load(filename));
        return arr;
    }

    // Carrega o recurso (se existir) e o envia para o navegador para fazer download atraves de um cabeçalho Content-Disposition.
    @GetMapping("/files/{filename:.+}")

    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    public String fileBytesToSHA256(byte[] arr) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256"); //Esta a criar uma instancia de SHA, MD2, MD5, SHA-256, SHA-384...
        byte[] hash = md.digest(arr); // fazer o hash do arr

        // bytes para hex
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    //Base64
    public static String fileBytesToBase64(byte[] arr) throws Exception {
        String base64String = Base64.getEncoder().encodeToString(arr); // pega na stream e le em bytes(stream.readAllBytes()) e faz encode em String para Base64 (Base64.getEncoder().encodeToString)

        return base64String;
    }

    //Manipula uma mensagem de várias partes files a entrega StorageService para salvar.
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException, IOException, Exception {

        //storageService.store(file);
        byte[] arrBytes = file.getBytes();

        //String hash = fileBytesToSHA256(fileConvertToBytes(file.getOriginalFilename()));
        //String base64File = fileBytesToBase64(fileConvertToBytes(file.getOriginalFilename()));
        String hash = fileBytesToSHA256(arrBytes);
        String base64File = fileBytesToBase64(arrBytes);
        JsonUtils.CreateJSON(hash, base64File);
        System.out.println("O utilizador submeteu um ficheiro!!");

        redirectAttributes.addFlashAttribute("message", "The file :  " + file.getOriginalFilename() + " was uploaded with sucess!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
