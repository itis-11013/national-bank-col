package ru.itis.sem_col.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itis.sem_col.models.Contract;
import ru.itis.sem_col.models.Product;
import ru.itis.sem_col.repositories.AccountRepository;
import ru.itis.sem_col.repositories.ContractRepository;
import ru.itis.sem_col.repositories.ProductRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
@Transactional
public class ContractServiceDetails implements ContractService{
    @Autowired
    OrganizationDetailService organizationDetailService;
    @Autowired
    ContractRepository contractRepository;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public List<Contract> getAllContracts() throws JsonProcessingException {
        //contracts in bd
        List<Contract> contracts= contractRepository.getContracts();
        //contracts in context organization
        List<Contract> contractOrg =  new ArrayList<>();

        for (Contract c: contracts) {

            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = "http://188.93.211.195/central/contract/" + c.getInnerId().toString();
            ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl + "", String.class);
            ObjectMapper mapper =  new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            JsonNode buyer = data.path("buyer");
            JsonNode innerid = buyer.path("innerid");
            if(data.path("isPaid").asBoolean()){
                c.setDeleted(true);
                String datepay = data.path("paymentDate").asText().substring(0,19);
                LocalDateTime dateTime = LocalDateTime.parse(datepay);
                contractRepository.update(true, c.getInnerId(), dateTime);
            }

            if (Objects.equals(innerid.asText(), organizationDetailService.getOrganization().getInnerId().toString())){
                contractOrg.add(c);
            }
        }
        return contractOrg;
    }
    public void payContract(Contract contract) throws JsonProcessingException {

        String url = "http://188.93.211.195/central/payment";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject map = new JSONObject();
        map.put("contractid", contract.getInnerId());
        HttpEntity<String> request = new HttpEntity<String>(map.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        String personResultAsJsonStr =
                restTemplate.postForObject(url, request, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        //get contract/product from api

        RestTemplate getrestTemplate = new RestTemplate();
        String fooResourceUrl = "http://188.93.211.195/central/contract/" + contract.getInnerId().toString();
        ResponseEntity<String> response = getrestTemplate.getForEntity(fooResourceUrl + "", String.class);
        ObjectMapper mapper =  new ObjectMapper();
        JsonNode getroot = mapper.readTree(response.getBody());
        JsonNode data = getroot.path("data");
        Double count = data.path("count").asDouble();
        Double price = productRepository.findByInnerId(UUID.fromString(data.path("productid").asText())).getPrice().doubleValue();
        Long total = Math.round(count*price);
        System.out.println(total + "/count: " + count + " /price: " + price);
        accountRepository.updateAmount(organizationDetailService.getOrganization().getAccounts().get(0).getAmount() - total, organizationDetailService.getOrganization());
}

    @Override
    public Contract addNewContract(String productUUID, Integer count) throws JsonProcessingException {
        Contract contract = new Contract();
        //get data from server
        String url = "http://188.93.211.195/central/contract";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject map = new JSONObject();
        map.put("productid", productUUID);
        map.put("count", count);
        map.put("buyerid", organizationDetailService.getOrganization().getInnerId().toString());
        System.out.println(map);
        HttpEntity<String> request = new HttpEntity<String>(map.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        String personResultAsJsonStr =
                restTemplate.postForObject(url, request, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        JsonNode data = root.path("data");
        System.out.println("InnerID New Contract in Server: "+ data.path("contractid"));
        UUID uuid = UUID.fromString(data.path("contractid").asText());
        String datefromsever = data.path("createdAt").asText();
        datefromsever = datefromsever.substring(0,19);
        LocalDateTime dateTime = LocalDateTime.parse(datefromsever);
        //LocalDateTime paymentDate = LocalDateTime.parse(root.path("");
        contract.setInnerId(uuid);
        contract.setContractDate(dateTime);
        //contract.setCount(root.path("count").asDouble());
        contract.setDeleted(data.path("isPaid").asBoolean());

        contract.setCount(data.path("count").asDouble());
        Product product = productRepository.findByInnerId(UUID.fromString(productUUID));
        System.out.println(product.getInnerId());
        contract.setProduct(productRepository.findByInnerId(UUID.fromString(productUUID)));
        contract.setBuyer(organizationDetailService.getOrganization());
        contract.setDeliveryDate(dateTime);
        contract.setPaymentDate(dateTime);
        contractRepository.save(contract);
        return contract;
    }
}
