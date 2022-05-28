package ru.itis.sem_col.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itis.sem_col.controllers.dto.AccountDto;
import ru.itis.sem_col.models.Account;
import ru.itis.sem_col.models.Organization;
import ru.itis.sem_col.services.AccountServiceImpl;
import ru.itis.sem_col.services.OrganizationDetailService;

@Controller
public class AccountController {
    @Autowired
    AccountServiceImpl accountservice;
    @Autowired
    OrganizationDetailService organizationDetailService;

    @GetMapping("/account")
    public String getOrganizationAccount(Model model) {
        AccountDto accountDto = new AccountDto();
        Organization organization = organizationDetailService.getOrganization();
        Account account = accountservice.contextAccount().get(0);
        model.addAttribute("account", account);
        model.addAttribute("organization", organization);
        model.addAttribute("accountDto", accountservice);
        return "account";
    }
}
