package org.example.repositories;

import org.example.models.Contact;

import java.util.List;

public interface ContactRepository {

    void addContact(Contact contacts);

    void editContact(Contact old_contact, Contact new_contact);

    void deleteContact(Contact contacts);

    List<Contact> searchContact(String criterion);

    void saveChanges();
}
