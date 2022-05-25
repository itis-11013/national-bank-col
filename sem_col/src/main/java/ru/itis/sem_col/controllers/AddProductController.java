package ru.itis.sem_col.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;
import ru.itis.sem_col.controllers.dto.AddProductDto;
import ru.itis.sem_col.controllers.dto.RegisterOrganizationDto;
import ru.itis.sem_col.models.Product;
import ru.itis.sem_col.repositories.ProductCatalogRepository;
import ru.itis.sem_col.repositories.ProductRepository;

@Controller
public class AddProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCatalogRepository productCatalogRepository;

//    @GetMapping("/product/add")
//    public String loginPage(Model model) {
//        return "addproduct";
//    }
    @GetMapping("/product/add")
    public String showRegistrationForm(WebRequest request, Model model) {
        AddProductDto AddProductDto = new AddProductDto();
        model.addAttribute("AddProductDto", AddProductDto);
        return "addproduct";
    }
}