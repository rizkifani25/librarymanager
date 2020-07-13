package com.nihil.librarymanager.librarymanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.nihil.librarymanager.librarymanager.model.Product;
import com.nihil.librarymanager.librarymanager.repositories.ProductRepository;

@RestController
public class ProductServiceController {
   private final Logger LOG = LoggerFactory.getLogger(getClass());
   private final ProductRepository productRepository;

   public ProductServiceController(ProductRepository productRepository) {
      this.productRepository = productRepository;
   }
 
   // POST API
   @RequestMapping(value = "/", method = RequestMethod.POST)
   public ResponseEntity<Object> createProduct(@RequestBody String queryOperation) {
      try {
         String responseBundleJSON = "", operationName = "", operationQuery = "", productId = "", productName = "";
         JSONObject operationVariable;
         List<Product> listProduct = null;
         JSONArray queryJSON = new JSONArray(queryOperation);
         for (int i = 0; i < queryJSON.length(); i++) {
            JSONObject queryObject = queryJSON.getJSONObject(i);
            operationName = queryObject.optString("operationName");
            switch (operationName) {
               case "ListBookQuery":
                  LOG.info("==========\t" + operationName + "\t==========");
                  operationQuery = queryObject.optString("query");
                  switch (operationQuery) {
                     case "FindById":
                        LOG.info("==========\tFindById\t==========");
                        operationVariable = queryObject.getJSONObject("variable");
                        productId = operationVariable.optString("productId");
                        listProduct = productRepository.findCustom(productId);
                        responseBundleJSON = "[{\"data\": {\"" + operationName + "\":{" + listProduct + "}}, \"message\": \"OK\"}]";
                        break;
                     case "FindAll":
                        LOG.info("==========\tFindAll\t==========");
                        listProduct = productRepository.findAll();
                        responseBundleJSON = "[{\"data\": {\"" + operationName + "\":{" + listProduct + "}}, \"message\": \"OK\"}]";
                        break;
                     case "InsertProduct":
                        LOG.info("==========\tInsertProduct\t==========");
                        operationVariable = queryObject.getJSONObject("variable");
                        productId = operationVariable.optString("id");
                        productName = operationVariable.optString("name");
                        listProduct = productRepository.findCustom(productId);
                        if(listProduct.size() != 0) {
                           responseBundleJSON = "[{\"data\": {\"" + operationName + "\":{}}, \"message\": \"Product with id " + productId + " already exist.\" }]";
                        }else {
                           Product newProduct = new Product();
                           newProduct.setId(productId);
                           newProduct.setName(productName);
                           productRepository.save(newProduct);
                           responseBundleJSON = "[{\"data\": {\"" + operationName + "\":{}}, \"message\": \"OK\"}]";
                        }
                        break;
                     default:
                        break;
                     }
                  break;
               default:
                  LOG.info("==========\t" + operationName + "\t==========");
                  responseBundleJSON = "[{\"data\": {\"" + operationName + "\":{}}, \"message\": \"OK\"}]";
                  break;
            }
         }
         LOG.info("==========\tStartingSetResponse\t==========");
         LOG.info(responseBundleJSON);
         LOG.info("==========\tEndingSetResponse\t==========");
         return new ResponseEntity<>(responseBundleJSON, HttpStatus.OK);
      } catch (Exception err) {
         LOG.info(err.toString());
         return new ResponseEntity<>("Request failed.", HttpStatus.BAD_REQUEST);
      }
   }
}