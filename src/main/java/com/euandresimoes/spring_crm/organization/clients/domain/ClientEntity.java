package com.euandresimoes.spring_crm.organization.clients.domain;

import java.util.UUID;

import com.euandresimoes.spring_crm.organization.organization_core.domain.OrganizationEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "clients", schema = "organization")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "cpf_cnpj", nullable = true)
    private String cpfCnpj;

    @Column(nullable = true)
    private String tel;

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    public ClientEntity() {
    }

    public ClientEntity(OrganizationEntity organization, String name, String description, String email, String cpfCnpj,
            String tel, ClientStatus status) {
        this.organization = organization;
        this.name = name;
        this.description = description;
        this.email = email;
        this.cpfCnpj = cpfCnpj;
        this.tel = tel;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

}
