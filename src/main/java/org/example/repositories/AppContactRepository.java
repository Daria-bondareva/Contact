package org.example.repositories;

import org.example.models.Contact;
import org.example.models.ContactsDataSource;

import java.util.ArrayList;
import java.util.List;

public class AppContactRepository implements ContactRepository {
    private final ContactsDataSource contactsDataSource;
    private final List<Contact> contacts;
    public AppContactRepository(ContactsDataSource contactsDataSource, List<Contact> contacts) {
        this.contactsDataSource = contactsDataSource;
        this.contacts = contacts != null ? contacts : new ArrayList<>();
    }

    @Override
    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    @Override
    public void editContact(Contact old_contact, Contact new_contact) {
        int index = contacts.indexOf(old_contact);
        contacts.set(index, new_contact);
    }

    @Override
    public void deleteContact(Contact contact) {
        contacts.remove(contact);
    }

    @Override
    public List<Contact> searchContact(String criterion) {
        List<Contact> searchResults = new ArrayList<>();
        for (Contact contact : contacts) {
            if(contact.fullName().name().contains(criterion)||
            contact.fullName().surName().contains(criterion)||
            contact.address().contains(criterion)||
            contact.phoneNumber().contains(criterion)||
            contact.email().contains(criterion));
            searchResults.add(contact);
        }
        return searchResults;
    }

    @Override
    public void saveChanges() {
        contactsDataSource.writeContacts(contacts);
    }
}
