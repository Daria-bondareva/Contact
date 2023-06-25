package org.example.repositories;

import org.example.models.Contact;

import java.util.List;

public interface ContactRepository {

    void addBook(Contact contacts);

    void editBook(Contact old_contact, Contact new_contact);

    void deleteBook(Contact contacts);

    List<Contact> searchContact(String criterion);

    void saveChanges();
}
