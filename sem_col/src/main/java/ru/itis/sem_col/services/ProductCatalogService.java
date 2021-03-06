package ru.itis.sem_col.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.sem_col.models.ProductCatalog;
import ru.itis.sem_col.repositories.OrganizationRepository;
import ru.itis.sem_col.repositories.ProductCatalogRepository;

import java.util.List;
import java.util.Set;

@Service
public class ProductCatalogService implements CatalogService{
    @Autowired
    ProductCatalogRepository productCatalogRepository;
    @Autowired
    OrganizationRepository organizationRepository;

    @Override
    public List<ProductCatalog> findByName(String name) {

        return productCatalogRepository.findByName(name);
    }

    @Override
    public List<ProductCatalog> findByCode(String code) {
        return productCatalogRepository.findByCode(code);
    }
    public List<ProductCatalog> ListAllProductCatalog(){
        return productCatalogRepository.findAlLimit();
    }

}
