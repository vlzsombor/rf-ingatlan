package hu.IB153I16.ingatlan.controller;

import hu.IB153I16.ingatlan.model.User;
import hu.IB153I16.ingatlan.repository.RealEstateRepository;
import hu.IB153I16.ingatlan.model.RealEstate;
import hu.IB153I16.ingatlan.repository.UserRepository;
import hu.IB153I16.ingatlan.utils.constant.FileUploadUtil;
import hu.IB153I16.ingatlan.utils.constant.URLPATH;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(URLPATH.REALESTATE_ROOT)
public class RealEstateController {
    @Autowired
    RealEstateRepository realEstateRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("{id}")
    public String index(@PathVariable("id") Long id, Model model) {
        RealEstate realEstate = realEstateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid real estate id:" + id));
        model.addAttribute("realEstate", realEstate);
        model.addAttribute("realEstatePhotos",null);
        try {
            if(realEstate.getPhotos() != null){
                System.out.println(FileUploadUtil.getAllImages(new File(realEstate.getPhotos())));
                model.addAttribute("realEstatePhotos", FileUploadUtil.getAllImages(new File(realEstate.getPhotos())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "realEstate/index";
    }


    @RequestMapping(value = "image/{id}/{imageName}",produces = "image/jpeg")
    @ResponseBody
    public byte[] getImage(@PathVariable(value = "id") String id,@PathVariable(value = "imageName") String imageName) throws IOException {
        Resource resource = new ClassPathResource("");
        File resourcesDirectory = new File(URLPATH.PHOTOS_RELATIVE_PATH + id + "/" + imageName);

        File serverFile = resourcesDirectory;
        System.out.println(serverFile);
        return Files.readAllBytes(serverFile.toPath());
    }

    @GetMapping(URLPATH.REALESTATE_UPLOAD)
    public String upload(Model model,RealEstate realEstate) {
        model.addAttribute("realEstate",realEstate);
        return "realEstate/upload";
    }

//    @PostMapping(URLPATH.REALESTATE_UPLOAD)
//    public String addActorPost(Model model,RealEstate realEstate) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalName = auth.getName();
//
//        realEstate.setUser(userRepository.findByEmail(currentPrincipalName));
//
//        realEstateRepository.save(realEstate);
//
//        return "redirect:/";
//    }

    @ModelAttribute("realEstate")
    public RealEstate realEstateModel(){
        return new RealEstate();
    }



    @PostMapping(URLPATH.REALESTATE_UPLOAD)
    public String addRealEstatePost(@ModelAttribute("realEstate") @Valid RealEstate realEstate, BindingResult result, @RequestParam("image") MultipartFile[] multipartFile) throws IOException {
        if (result.hasErrors() || multipartFile.length < 6){
            return "realEstate/upload";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = auth.getName();

        realEstate.setUser(userRepository.findByEmail(currentPrincipalName));

        realEstateRepository.save(realEstate);
        String uploadDir = URLPATH.PHOTOS_RELATIVE_PATH + realEstate.getId();
        realEstate.setPhotos(uploadDir);
        savePhoto(multipartFile,uploadDir,realEstate);


        realEstateRepository.save(realEstate);
        return "redirect:/";
    }

    private void savePhoto(MultipartFile[] multipartFile, String uploadDir,RealEstate realEstate) throws IOException {
        List<String> fileNames = new ArrayList<>();

        if(Files.exists(Paths.get(uploadDir))) {
            FileUtils.cleanDirectory(new File(uploadDir));
        }
        for (var x: multipartFile) {
            String fileName = StringUtils.cleanPath(x.getOriginalFilename());

            fileNames.add(fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, x);
        }
        realEstate.setMainPhotoName(FileUploadUtil.getAllImages(new File(realEstate.getPhotos())).get(0));
        System.out.println(realEstate.getMainPhotoName());
    }

    @PostMapping("update/{id}")
    public String addRealEstate(@ModelAttribute("id") Long id,@Valid RealEstate realEstate,  BindingResult result,@RequestParam("image") MultipartFile[] multipartFile) throws IOException {
        if (result.hasErrors() || multipartFile.length < 6){
            return "realEstate/upload";
        }

        RealEstate realEstateRep = realEstateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie id:" + id));
        realEstate.setUser(realEstateRep.getUser());
        realEstate.setPhotos(realEstateRep.getPhotos());
        realEstate.setId(id);

        savePhoto(multipartFile,realEstateRep.getPhotos(),realEstate);


        realEstateRepository.save(realEstate);
        //model.addAttribute("realEstate", realEstate);
        return "redirect:/";
    }

    @GetMapping("update/{id}")
    public String updateRealEstate(@PathVariable("id") Long id, Model model) {
        RealEstate realEstate = realEstateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie id:" + id));
        model.addAttribute("realEstate", realEstate);
        return "realEstate/update";
    }

    @GetMapping("delete/{id}")
    public String deleteRealEstate(@PathVariable("id") long id, Model model) {

        RealEstate realEstate = realEstateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        realEstateRepository.delete(realEstate);
        //model.addAttribute("students", realEstateRepository.findAll());
        return "redirect:/profile";
    }


}

