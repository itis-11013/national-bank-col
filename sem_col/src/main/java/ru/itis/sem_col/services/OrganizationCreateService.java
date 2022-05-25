package ru.itis.sem_col.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itis.sem_col.controllers.dto.RegisterOrganizationDto;
import ru.itis.sem_col.models.Account;
import ru.itis.sem_col.models.Country;
import ru.itis.sem_col.models.NationalBank;
import ru.itis.sem_col.models.Organization;
import ru.itis.sem_col.repositories.AccountRepository;
import ru.itis.sem_col.repositories.CountryRepository;
import ru.itis.sem_col.repositories.NationalBankRepository;
import ru.itis.sem_col.repositories.OrganizationRepository;

import javax.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Service
@Transactional
public class OrganizationCreateService implements IOrgService{
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NationalBankRepository nationalBankRepository;

    @Override
    public void registerNewOrganization(RegisterOrganizationDto organizationDto) throws JsonProcessingException {
        //get data from server
        String url = "http://188.93.211.195/central/organization";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject map = new JSONObject();
        map.put("name", organizationDto.getName());
        map.put("country", "co");
        map.put("address", organizationDto.getAddress());
        map.put("url", "--");
        System.out.println(map);
        HttpEntity<String> request = new HttpEntity<String>(map.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        String personResultAsJsonStr =
                restTemplate.postForObject(url, request, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        JsonNode innerId = root.path("innerid");
        System.out.println("InnerID New Org in Server: "+ innerId);

        ///// add organization to BD
        UUID uuid = UUID.fromString(innerId.asText());
        Country country = countryRepository.findByCode("co");
        Organization organization = new Organization();
        organization.setName(organizationDto.getName());
        organization.setAddress(organizationDto.getAddress());
        organization.setPassword(organizationDto.getPassword());
        organization.setInnerId(uuid);
        organization.setCountry(country);
        System.out.println(organization);
        organizationRepository.save(organization);
        /// add account to BD

        Account account = new Account();
        account.setOrganization(organizationRepository.findByName(organizationDto.getName()));
        account.setCurrency("COP");
        account.setAmount((long) ThreadLocalRandom.current().nextInt(500000, 100000000));
        NationalBank nationalBank = nationalBankRepository.findNationalBankByAddress("street 1281 bogota").get(0);
        System.out.println(nationalBank.getCountry());
        System.out.println(nationalBank.getAddress());
        System.out.println(nationalBank.getInnerId());
        account.setNational_bank(nationalBank);
        accountRepository.save(account);
    }
}
