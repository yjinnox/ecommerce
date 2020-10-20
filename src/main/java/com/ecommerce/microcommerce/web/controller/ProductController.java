package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Api( produces = "API pour es opérations CRUD sur les produits.")
@RestController
public class ProductController {

    @Autowired
    ProductDao productDao;

    @GetMapping(value="/products")
    public MappingJacksonValue getProduct() {

        Iterable<Product> listeDeProduits = productDao.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeFilters = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue productFiltres = new MappingJacksonValue(listeDeProduits);

        productFiltres.setFilters(listDeFilters);

        return productFiltres;

    }

    @GetMapping("test/products/{prixLimit}")
    public MappingJacksonValue testeDeRequetes(@PathVariable double prixLimit) {

        Iterable<Product> listeDeProduits = productDao.findByPriceGreaterThan(400.00);

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeFilters = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue productFiltres = new MappingJacksonValue(listeDeProduits);

        productFiltres.setFilters(listDeFilters);

        return productFiltres;
    }

    @GetMapping(value = "test/mot/products/{recherche}")
    public MappingJacksonValue testeDeRequetes(@PathVariable String recherche) {
        Iterable<Product> listeDeProduits = productDao.findByNameLike("%"+recherche+"%");
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeFilters = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue productFiltres = new MappingJacksonValue(listeDeProduits);

        productFiltres.setFilters(listDeFilters);

        return productFiltres;
    }
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/products/{id}")
    public Product getProductById(@PathVariable int id) {

        Product product = productDao.findById(id);
        if(product==null){
            throw new ProduitIntrouvableException("Le produit avec l'id " + id + " " +
                "est INTROUVABLE. Écran Bleu si je pouvais.");
        }
        return product;
    }

    @PostMapping(value = "/products/save")
    public ResponseEntity<Void> saveProduct(@Valid @RequestBody Product product){

        Product product1 = productDao.save(product);
        if (product1 == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product1.getId())
                .toUri();

        System.out.println(location);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping (value = "/products/delete/{id}")
    public void supprimerProduit(@PathVariable int id) {

        productDao.deleteById(id);
    }
}
