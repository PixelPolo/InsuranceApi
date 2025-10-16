// https://docs.hibernate.org/orm/current/userguide/html_single/

package com.ricci.insuranceapi.insurance_api.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "client")
public class Client {

    // -- CONSTRUCTOR --

    public Client() {
    }

    public Client(UUID clientId, String phone, String email,
            String name, boolean isDeleted, LocalDate deletionDate) {
        this.clientId = clientId;
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.isDeleted = isDeleted;
        this.deletionDate = deletionDate;
    }

    // -- ATTRIBUTES --

    @Id
    @GeneratedValue
    @Column(name = "client_id", updatable = false, nullable = false)
    private UUID clientId;

    @Column(length = 16)
    private String phone;

    @Column(length = 128, unique = true)
    private String email;

    @Column(length = 64)
    private String name;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "deletion_date")
    private LocalDate deletionDate;

    // -- GETTERS AND SETTERS --

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDate getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(LocalDate deletionDate) {
        this.deletionDate = deletionDate;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + clientId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}
