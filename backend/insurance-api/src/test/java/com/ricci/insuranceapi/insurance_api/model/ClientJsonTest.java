/*
 * Since Client is an abstract class,
 * Jackson cannot directly serialize or deserialize it.
 */

// package com.ricci.insuranceapi.insurance_api.model;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.json.JsonTest;
// import org.springframework.boot.test.json.JacksonTester;

// import java.io.IOException;

// import static org.assertj.core.api.Assertions.assertThat;

// /*
// * This class tests the JSON serialization and deserialization
// * of the Client entity using Jackson.
// * It ensures that the JSON format matches the expected API structure.
// * Inspired by Spring Academy materials.
// */
// @JsonTest
// class ClientJsonTest {

// @Autowired
// private JacksonTester<Client> json;

// @Autowired
// private JacksonTester<Client[]> jsonList;

// // Serialization -> Single Client
// @Test
// void shouldSerializeSingleClient() throws IOException {
// Client[] clients = jsonList.read("clients.json").getObject();
// Client firstClient = clients[0];

// assertThat(json.write(firstClient)).isStrictlyEqualToJson("""
// {
// "clientId": "550e8400-e29b-41d4-a716-446655440000",
// "phone": "+41791234567",
// "email": "alice@example.com",
// "name": "Alice Dupont",
// "isDeleted": false,
// "deletionDate": null
// }
// """);
// }

// // Deserialization -> Single Client
// @Test
// void shouldDeserializeSingleClient() throws IOException {
// Client[] clients = jsonList.read("clients.json").getObject();
// Client firstClient = clients[0];

// assertThat(firstClient.getName()).isEqualTo("Alice Dupont");
// assertThat(firstClient.getEmail()).isEqualTo("alice@example.com");
// assertThat(firstClient.getIsDeleted()).isFalse();
// }

// // Deserialization -> Client List
// @Test
// void shouldDeserializeClientsList() throws IOException {
// Client[] clients = jsonList.read("clients.json").getObject();

// assertThat(clients).hasSize(3);
// assertThat(clients[1].getName()).isEqualTo("Bob Martin");
// assertThat(clients[2].getName()).isEqualTo("Entreprise SA");
// }

// // Serialization -> Client List
// @Test
// void shouldSerializeClientsListToJson() throws IOException {
// Client[] clients = jsonList.read("clients.json").getObject();

// assertThat(jsonList.write(clients)).isStrictlyEqualToJson("clients.json");
// }
// }
