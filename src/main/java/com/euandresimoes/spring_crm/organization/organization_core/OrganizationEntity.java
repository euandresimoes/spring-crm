package com.euandresimoes.spring_crm.organization.organization_core;

import java.util.List;
import java.util.UUID;

import com.euandresimoes.spring_crm.organization.clients.ClientEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "organizations", schema = "organization")
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(length = 15, nullable = false)
    private String name;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ClientEntity> clients;

    public OrganizationEntity() {
    }

    public OrganizationEntity(UUID userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClientEntity> getClients() {
        return clients;
    }

    public void setClients(List<ClientEntity> clients) {
        this.clients = clients;
    }

}
