package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.converters.GsonConverter;
import org.example.converters.JsonConverter;
import org.example.models.Contact;
import org.example.models.ContactsDataSource;
import org.example.models.FullName;
import org.example.models.LocalDateAdapter;
import org.example.repositories.AppContactRepository;
import org.example.repositories.ContactRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String JSON_FILE_PATH = "contacts.json";
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        JsonConverter gsonConverter = new GsonConverter(gson);
        Path filePath = Paths.get(JSON_FILE_PATH);
        if(!Files.exists(filePath)){
            try{
                Files.createFile(filePath);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        ContactsDataSource contactsDataSource = new ContactsDataSource(gsonConverter, filePath);
        List<Contact> contacts = contactsDataSource.readContacts();
        ContactRepository contactRepository = new AppContactRepository(contactsDataSource, contacts != null ? contacts : new ArrayList<>());
        while (true){
            System.out.println("""
                    Меню:
                    0. Вийти 
                    1. Додати контакт
                    2. Редагувати контакт 
                    3. Видалити контакт
                    4. Пошук контакту
                    5. Сортування контактів
                    """);
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice){
                case 0 -> System.exit(0);
                case 1 -> addContact(contactRepository);
                case 2 -> editContact(contactRepository);
                case 3 -> deleteContact(contactRepository);
                case 4 -> searchContact(contactRepository);
                case 5 -> sortContacts(contactRepository);
                default -> System.out.println("Будь ласка, виберіть існуючий номер дії");
            }
        }
    }
    private static void addContact(ContactRepository contactRepository){
        System.out.println("Введіть ім'я:");
        String name = scanner.nextLine();
        System.out.println("Введіть прізвище:");
        String surname = scanner.nextLine();
        System.out.println("Введіть номер телефону:");
        String phoneNumber = scanner.nextLine();
        System.out.println("Введіть електронну пошту:");
        String email = scanner.nextLine();
        System.out.println("Введіть дату народження (у форматі yyyy-MM-dd):");
        String birthdayString = scanner.nextLine();
        LocalDate birthday = LocalDate.parse(birthdayString);
        System.out.println("Введіть адресу:");
        String address = scanner.nextLine();

        Contact newContact = new Contact(new FullName(name, surname), phoneNumber, email, birthday, address);
        contactRepository.addContact(newContact);
        contactRepository.saveChanges();
        System.out.println("Контакт додано.");
    }
    private static void editContact(ContactRepository contactRepository){
        System.out.println("Введіть ім'я контакта, яке хочете відредагувати: ");
        String searchName = scanner.nextLine();
        List<Contact> searchResult = contactRepository.searchContact(searchName);

        if(searchResult.isEmpty()){
            System.out.println("Контакта з таким іменем не існує.");
        } else{
            System.out.println("Знайдені контакти: ");
            displayContacts(searchResult);

            System.out.println("Введіть номер контакта, який потрібно змінити: ");
            int contactIndex = scanner.nextInt();
            scanner.nextLine();

            if(contactIndex >= 0 && contactIndex < searchResult.size()){
                Contact oldContact = searchResult.get(contactIndex);

                System.out.println("Введіть нове ім'я:");
                String newName = scanner.nextLine();
                System.out.println("Введіть нове прізвище:");
                String newSurname = scanner.nextLine();
                System.out.println("Введіть новий номер телефону:");
                String newPhoneNumber = scanner.nextLine();
                System.out.println("Введіть нову електронну пошту:");
                String newEmail = scanner.nextLine();
                System.out.println("Введіть нову дату народження (у форматі yyyy-MM-dd):");
                String newBirthdayString = scanner.nextLine();
                LocalDate newBirthday = LocalDate.parse(newBirthdayString);
                System.out.println("Введіть нову адресу:");
                String newAddress = scanner.nextLine();

                Contact newContact = new Contact(new FullName(newName, newSurname), newPhoneNumber, newEmail, newBirthday, newAddress);
                contactRepository.editContact(oldContact, newContact);
                contactRepository.saveChanges();
                System.out.println("Контакт відредаговано. ");

            } else{
                System.out.println("Невірно набраний номер контакта.");
            }
        }
    }
    private static void deleteContact(ContactRepository contactRepository) {
        System.out.println("Введіть ім'я контакту, який потрібно видалити:");
        String searchName = scanner.nextLine();
        List<Contact> searchResult = contactRepository.searchContact(searchName);

        if (searchResult.isEmpty()) {
            System.out.println("Контакти з таким іменем не знайдені.");
        } else {
            System.out.println("Знайдені контакти:");
            displayContacts(searchResult);

            System.out.println("Введіть номер контакту, який потрібно видалити:");
            int contactIndex = scanner.nextInt();
            scanner.nextLine();

            if (contactIndex >= 0 && contactIndex < searchResult.size()) {
                Contact contactToDelete = searchResult.get(contactIndex);
                contactRepository.deleteContact(contactToDelete);
                contactRepository.saveChanges();
                System.out.println("Контакт видалено.");
            } else {
                System.out.println("Невірно набраний номер контакта.");
            }
        }
    }

    private static void searchContact(ContactRepository contactRepository){
        System.out.println("Введіть критерій пошуку:");
        String criterion = scanner.nextLine();
        List<Contact> searchResults = contactRepository.searchContact(criterion);

        if(searchResults.isEmpty()){
            System.out.println("Контакти з таким іменем не знайдені.");
        } else{
            System.out.println("Знайдені контакти:");
            displayContacts(searchResults);
        }
    }

    private static void sortContacts(ContactRepository contactRepository){
        List<Contact> contacts = contactRepository.searchContact("");
        contacts.sort((c1, c2) -> c1.fullName().name().compareToIgnoreCase(c2.fullName().name()));
        displayContacts(contacts);
    }

    private static void displayContacts(List<Contact> contacts){
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            System.out.println("Контакт " + (i + 1) + ":");
            System.out.println("Ім'я: " + contact.fullName().name());
            System.out.println("Прізвище: " + contact.fullName().surName());
            System.out.println("Номер телефону: " + contact.phoneNumber());
            System.out.println("Електронна пошта: " + contact.email());
            System.out.println("Дата народження: " + contact.birthday());
            System.out.println("Адреса: " + contact.address());
            System.out.println();
        }
    }
}