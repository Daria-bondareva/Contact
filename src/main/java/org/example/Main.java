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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.Path.*;

public class Main {
    private static final String DATA_FILE_PATH = "contacts.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        JsonConverter gsonConverter = new GsonConverter(gson);
        Path pathFile = of(DATA_FILE_PATH);
        ContactsDataSource contactsDataSource = new ContactsDataSource(gsonConverter, pathFile);
        if (!Files.exists(pathFile)) contactsDataSource.writeContacts(List.of());
        List<Contact> contacts = contactsDataSource.readContacts();
        ContactRepository contactRepository = new AppContactRepository(contactsDataSource, contacts);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("""
                            Меню:
                            0. Вийти з програми
                            1. Додати контакт
                            2. Редагувати контакт
                            3. Видалити контакт
                            4. Пошук контакту
                            5. Сортування контактів
                            6. Усі контакти
                            """);
            System.out.print("Введіть номер операції: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "0" -> System.exit(0);
                case "1" -> addContact(scanner, contactRepository);
                case "2" -> editContact(scanner, contactRepository);
                case "3" -> deleteContact(scanner, contactRepository);
                case "4" -> searchContact(scanner, contactRepository);
                case "5" -> sortContacts(contactRepository);
                case "6" -> showAllContacts(contactRepository);
                default -> System.out.println("Неправильний вибір. Спробуйте ще раз.");
            }
            System.out.println();
        }
    }

    private static void addContact(Scanner scanner, ContactRepository contactRepository) {
        System.out.print("Введіть ім'я: ");
        String name = scanner.nextLine();

        System.out.print("Введіть прізвище: ");
        String surname = scanner.nextLine();

        System.out.print("Введіть номер телефону: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Введіть електронну адресу: ");
        String email = scanner.nextLine();

        System.out.print("Введіть дату народження (у форматі dd.MM.yyyy): ");
        String birthdayString = scanner.nextLine();
        LocalDate birthday = LocalDate.parse(birthdayString, DATE_FORMATTER);

        System.out.print("Введіть адресу: ");
        String address = scanner.nextLine();

        Contact newContact = new Contact(new FullName(name, surname), phoneNumber, email, birthday, address);
        contactRepository.addContact(newContact);

        contactRepository.saveChanges();
        System.out.println("Контакт успішно доданий.");
    }

    private static void editContact(Scanner scanner, ContactRepository contactRepository) {
        System.out.print("Введіть ім'я контакту, який потрібно редагувати: ");
        String searchName = scanner.nextLine();

        List<Contact> searchResults = contactRepository.searchContact(searchName);

        if (!searchResults.isEmpty()) {
            System.out.println("Знайдено " + searchResults.size() + " контакт(ів).");
            System.out.println("Виберіть номер контакту для редагування:");

            for (int i = 0; i < searchResults.size(); i++) {
                Contact contact = searchResults.get(i);
                System.out.println((i + 1) + ". " + contact.fullName().name() + " " + contact.fullName().surName());
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= 1 && choice <= searchResults.size()) {
                Contact selectedContact = searchResults.get(choice - 1);

                System.out.print("Введіть нове ім'я: ");
                String name = scanner.nextLine();

                System.out.print("Введіть нове прізвище: ");
                String surname = scanner.nextLine();

                System.out.print("Введіть новий номер телефону: ");
                String newPhoneNumber = scanner.nextLine();

                System.out.print("Введіть нову електронну адресу: ");
                String email = scanner.nextLine();

                System.out.print("Введіть нову дату народження (у форматі dd.MM.yyyy): ");
                String birthdayString = scanner.nextLine();
                LocalDate birthday = LocalDate.parse(birthdayString, DATE_FORMATTER);

                System.out.print("Введіть нову адресу: ");
                String address = scanner.nextLine();

                Contact updatedContact = new Contact(new FullName(name, surname), newPhoneNumber, email, birthday, address);
                contactRepository.editContact(selectedContact, updatedContact);
                contactRepository.saveChanges();
                System.out.println("Контакт успішно оновлений.");
            } else {
                System.out.println("Неправильний вибір контакту.");
            }
        } else {
            System.out.println("Контакти з таким номером телефону не знайдено.");
        }
    }

    private static void deleteContact(Scanner scanner, ContactRepository contactRepository) {
        System.out.print("Введіть ім'я контакту, який потрібно видалити: ");
        String searchName = scanner.nextLine();

        List<Contact> searchResults = contactRepository.searchContact(searchName);

        if (!searchResults.isEmpty()) {
            System.out.println("Знайдено " + searchResults.size() + " контакт(ів).");
            System.out.println("Виберіть номер контакту для видалення:");

            for (int i = 0; i < searchResults.size(); i++) {
                Contact contact = searchResults.get(i);
                System.out.println((i + 1) + ". " + contact.fullName().name() + " " + contact.fullName().surName());
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= 1 && choice <= searchResults.size()) {
                Contact selectedContact = searchResults.get(choice - 1);
                contactRepository.deleteContact(selectedContact);
                contactRepository.saveChanges();
                System.out.println("Контакт успішно видалений.");
            } else {
                System.out.println("Неправильний вибір контакту.");
            }
        } else {
            System.out.println("Контакти з таким номером телефону не знайдено.");
        }
    }

    private static void searchContact(Scanner scanner, ContactRepository contactRepository) {
        System.out.print("Введіть критерій пошуку: ");
        String criterion = scanner.nextLine();

        List<Contact> searchResults = contactRepository.searchContact(criterion);

        if (!searchResults.isEmpty()) {
            System.out.println("Знайдено " + searchResults.size() + " контакт(ів):");

            for (int i = 0; i < searchResults.size(); i++) {
                Contact contact = searchResults.get(i);
                System.out.println("Контакт " + (i + 1) + ":\n" +
                        "Ім'я: " + contact.fullName().name() + "\n" +
                        "Прізвище: " + contact.fullName().surName() + "\n" +
                        "Номер телефону: " + contact.phoneNumber() + "\n" +
                        "Електронна адреса: " + contact.email() + "\n" +
                        "Дата народження: " + contact.birthday().format(DATE_FORMATTER) + "\n" +
                        "Адреса: " + contact.address() + "\n");
            }
        } else {
            System.out.println("Контакти з таким критерієм не знайдено.");
        }
    }

    private static void sortContacts(ContactRepository contactRepository) {
        System.out.println("""
                        Виберіть поле для сортування:");
                        1. Ім'я
                        2. Прізвище
                        3. Номер телефону
                        4. Електронна адреса
                        6. Адреса
                        5. Дата народження
                        Введіть номер поля:""");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        Comparator<Contact> comparator;

        switch (choice) {
            case "1" -> comparator = Comparator.comparing(c -> c.fullName().name().toLowerCase());
            case "2" -> comparator = Comparator.comparing(c -> c.fullName().surName().toLowerCase());
            case "3" -> comparator = Comparator.comparing(Contact::phoneNumber);
            case "4" -> comparator = Comparator.comparing(Contact::email);
            case "5" -> comparator = Comparator.comparing(Contact::birthday);
            case "6" -> comparator = Comparator.comparing(Contact::address);
            default -> {
                System.out.println("Неправильний вибір поля.");
                return;
            }
        }
        contactRepository.sortContacts(comparator);
        System.out.println("Контакти успішно відсортовано.");
        showAllContacts(contactRepository);
    }

    private static void showAllContacts(ContactRepository contactRepository) {
        List<Contact> allContacts = contactRepository.searchContact("");
        System.out.println("Всі контакти:");

        for (int i = 0; i < allContacts.size(); i++) {
            Contact contact = allContacts.get(i);
            System.out.println("Контакт " + (i + 1) + ":\n" +
                    "Ім'я: " + contact.fullName().name() + "\n" +
                    "Прізвище: " + contact.fullName().surName() + "\n" +
                    "Номер телефону: " + contact.phoneNumber() + "\n" +
                    "Електронна адреса: " + contact.email() + "\n" +
                    "Дата народження: " + contact.birthday().format(DATE_FORMATTER) + "\n" +
                    "Адреса: " + contact.address() + "\n");
        }
    }
}