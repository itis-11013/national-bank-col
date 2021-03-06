package ru.itis.sem_col.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import ru.itis.sem_col.controllers.dto.ProductDto;
import ru.itis.sem_col.models.ProductCatalog;
import ru.itis.sem_col.models.Units;
import ru.itis.sem_col.services.ProductCatalogService;
import ru.itis.sem_col.services.ProductServiceImpl;
import ru.itis.sem_col.services.UnitServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Controller
public class ProductController {
    @Autowired
    ProductCatalogService catalogServiceimpl;

    @Autowired
    UnitServiceImpl unitServiceImpl;

    @Autowired
    ProductServiceImpl productService;

    @GetMapping("/product/add")
    public String addProduct(WebRequest request, Model model) {
        ProductDto productDto = new ProductDto();
        List<ProductCatalog> products = catalogServiceimpl.ListAllProductCatalog();
        List<Units> units = unitServiceImpl.listAllUnits();
        model.addAttribute("product", products );
        model.addAttribute("units", units );

        model.addAttribute("productredy", productDto);

        return "addproduct";
    }

    @PostMapping("/product/add")
    public ModelAndView registerUserAccount(@ModelAttribute("product") @Valid ProductDto productDto) throws JsonProcessingException {
        productService.registerNewProduct(productDto);

        return new ModelAndView("excelent", "product", productDto);
    }


}
