package main.lsea;

import main.database.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Class that extends Item abstract class<br>
 * It is class about cars<br>
 * implements Comparator interface, Serializable<br>
 * All setters and getters are automatically generated by lombok.<br>
 * NoArgsConstructor is also generated automatically.
 *
 * @TODO New methods
 * @author Patryk Dunajewski
 *
 */
@Getter @Setter @NoArgsConstructor
@ManagedBean(name = "car",eager = true)
@RequestScoped
public class Car extends Item implements Comparator<Car>,Serializable{

    /** power -> power of the car(private int)*/
    @NotNull(message = "\tPower cannot be null")
    @Min(value = 0, message = "\tPower cannot be negative")
    private int power;
    /** year -> year of production of the car(private int)*/
    @NotNull(message = " Year cannot be null")
    @Min(value = 0, message = "\tProduction year cannot be negative")
    private int year;
    /** passengers -> number of passengers(private int)*/
    @NotNull(message = "\tPassengers number cannot be null")
    @Min(value = 0, message = "\tNumber of passengers cannot be negative")
    private int passengers;
    /** transmission -> type of transmission(private String)*/
    @NotNull(message = "\tTransmission cannot be null")
    private String transmission;
    /** country -> country of production(private String)*/
    @NotNull(message = "\tCountry cannot be null")
    private String country;


    /**
     * Basic constructor with given attributes, used in load, save and creating offers of the car
     * @param nameOI name of an object (protected String)
     * @param brand name of a brand of the object (protected String)
     * @param auction do you want to have auction or buy now (protected int)
     * @param price price in PLN(protected float)
     * @param weight weight of the object in Kg(protected float)
     * @param account Reference to User that poses this item(protected LSEA)
     * @param available Is it still available. After buying an object it becomes 0. And when it is available it is 1 (protected int)
     * @param year year of production of the car(private int)
     * @param power power of the car(private int)
     * @param passengers number of passengers(private int)
     * @param transmission type of transmission(private String)
     * @param country country of production(private String)
     */
    public Car(String nameOI, String brand, int auction, float price, float weight, LSEA account, int available,
               int year, int power, int passengers, String transmission, String country) {
        super(nameOI, brand, auction, price, weight, account, available);
        this.power = power;
        this.year = year;
        this.passengers = passengers;
        this.transmission = transmission;
        this.country = country;
    }

    /**
     * CreateOfferCar method creating an offer of a car. This is one of the most important methods in this class.
     * @param account list of all accounts (type LSEA)
     * @return constructor of the Car class (type Car)
     */
    public Car createOfferCar(LSEA account, EntityManager eM, DB_Handling db_H) {
        String nameOI, brand, transmission, country;
        int is_auction, power, year, passengers;
        float price, weight;

        Scanner S = new Scanner(System.in);
        System.out.println("New offer will be created.");
        System.out.println("Name of item: ");
        nameOI = S.nextLine();
        System.out.println("Brand: ");
        brand = S.nextLine();
        System.out.println("Type of transmission: ");
        transmission = S.nextLine();
        System.out.println("Production country: ");
        country = S.nextLine();
        System.out.println("Do you want to have auction: (1-true/0-false)");
        is_auction = Integer.parseInt(S.nextLine());
        System.out.println("Price: ");
        price = Float.parseFloat(S.nextLine());
        System.out.println("Weight: ");
        weight = Float.parseFloat(S.nextLine());
        System.out.println("Power of the car: ");
        power = Integer.parseInt(S.nextLine());
        System.out.println("Year of production: ");
        year = Integer.parseInt(S.nextLine());
        System.out.println("Number of passengers: ");
        passengers = Integer.parseInt(S.nextLine());
        DB_Profile profile = eM.getReference(DB_Profile.class, db_H.getDbP().size());
        DB_Car car = new DB_Car(nameOI, brand, is_auction, price, weight, profile, 1, year, power, passengers, transmission, country);
        db_H.getDbC().add(car);
        EntityTransaction tx = eM.getTransaction();
        tx.begin();
        eM.persist(car);
        tx.commit();

        System.out.println("Car offer was successfully created.");
        return new Car(nameOI, brand, is_auction, price, weight, account, 1, year, power, passengers, transmission, country);
    }

    /*** Basic method checking information about given item*/
    @Override public void getInfo() {
        if(this.getIs_available() != 0) {
            System.out.println("Name of item: " + this.getNameOI());
            System.out.println("Brand: " + this.getBrand());
            System.out.println("Is is an auction: " + this.getIs_auction());
            System.out.println("Price: " + this.getPrice()+" PLN");
            System.out.println("Weight: " + this.getWeight()+" Kg");
            System.out.println("Owner nick: " + this.getAccount().getNick());
            System.out.println("Power of the car: " + this.getPower());
            System.out.println("Year of production: " + this.getYear());
            System.out.println("Number of passengers: " + this.getPassengers());
            System.out.println("Type of transmission: " + this.getTransmission());
            System.out.println("Production country: " + this.getCountry());
        }
        else System.out.println("Car is not available");
    }

    /*** Unique method for car, overview of the car<br>
     * 1. Power>=300 it Power-(Current Year- year of the car)<br>
     * 2. Power>=150 it Power-(Current Year- (year of the car/2))<br>
     * 3. Otherwise Power + 1*/
    public void carOverview() {
        int a = (2020 - this.getYear());
        if(this.getPower() >= 300)this.setPower(this.getPower() - a);
        else if(this.getPower() >= 150)this.setPower(this.getPower() - a/2);
        else this.setPower(this.getPower() + 1);
    }

    /**
     * method to buy the items<br>
     * 1. Item is available and affordable + deep cloning the old offer and copy it to backup list<br>
     * 2. Item is available but not affordable<br>
     * 3. Item is not available<br>
     * @TODO In future there would be also auction so we can change the price
     * @param account list with all of the accounts (type LSEA)
     * @throws CloneNotSupportedException
     */
    public Car buyItem(LSEA account) throws CloneNotSupportedException {
        if(this.getIs_available() == 1) {
            if(account.getMoney() >= this.getPrice()) {
                Car temp= (Car) this.clone();
                account.setMoney(account.getMoney() - this.getPrice());
                this.getAccount().setMoney(this.getAccount().getMoney() + this.getPrice());
                this.setAccount(account);
                this.setIs_available(0);
                System.out.println("Successfully bought an item");
                return temp;
            }
            else System.out.println("You don't have enough money");
        }
        else System.out.println("This item in not available any more");
        return this;
    }

    /**
     * Main just to check methods from car class
     * It is just for testing some class methods
     */
    public static void main(String[] args) {
        List<LSEA>profiles  =new ArrayList<>();
        List<Car>cars = new ArrayList<>();
        LSEA newObject = new LSEA();
        profiles=newObject.load("profiles");
        Car nCar = new Car();
        cars=nCar.load(profiles);
        for(int i=0; i<cars.size(); i++) {
            cars.get(i).getInfo();
        }
        for(int i=0; i<cars.size(); i++) {
            cars.get(i).carOverview();
        }
        for(int i=0; i<cars.size(); i++) {
            cars.get(i).getInfo();
        }
    }

    /*** save from Item class*/
    public void save() {}

    /**
     * overloaded save from Item class
     * @param db_H object of class necessary for database handling
     * @param cars list of all offers of cars (List<Car>)
     */
    public void save(DB_Handling db_H, List<Car>cars, String uPath) {
        File file = new File(uPath + ".txt");
        try {
            FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            FileLock fl = fc.tryLock();
            FileWriter output = new FileWriter(file);
            if(fl != null) {
                System.out.println("File is locked");
                for (int i = 0; i < cars.size(); i++) {
                    int flag = -1;
                    if (cars.get(i).getAccount() != null) {
                        output.write(cars.get(i).getNameOI() + "\n");
                        output.write(cars.get(i).getBrand() + "\n");
                        output.write(cars.get(i).getIs_auction() + "\n");
                        output.write(cars.get(i).getPrice() + "\n");
                        output.write(cars.get(i).getWeight() + "\n");
                        for (int j = 0; j < db_H.getDbC().size(); j++) {
                            if (db_H.getDbC().get(j).getAccount().getNick() == cars.get(i).getAccount().getNick()) {
                                flag = db_H.getDbC().get(j).getAccount().getId();
                                output.write(flag + "\n");
                                break;
                            }
                        }
                        if (flag == -1) {
                            output.write((db_H.getDbP().size() + 1) + "\n");
                        }
                        output.write(cars.get(i).getIs_available() + "\n");
                        output.write(cars.get(i).getYear() + "\n");
                        output.write(cars.get(i).getPower() + "\n");
                        output.write(cars.get(i).getPassengers() + "\n");
                        output.write(cars.get(i).getTransmission() + "\n");
                        output.write(cars.get(i).getCountry() + "\n");
                    }
                }
                fl.release();
                System.out.println("File is unlocked");
                System.out.println("Data were saved to Txt file");
            }
            output.close();
        }catch(IOException ex) {
            System.out.print(ex.getMessage());
        }
    }

    /**
     * Loading values from the .txt file
     * @param db_H object of class necessary for database handling
     * @param profiles list of all accounts (type LSEA)
     * @return list with cars (type Cars)
     */
    public ArrayList<Car> loadCarDB(DB_Handling db_H, List<LSEA> profiles){
        ArrayList<Car>cars =new ArrayList<>();
        ArrayList<DB_Car> dbC=db_H.getDbC();
        String nameOI, brand,transmission,country,account;
        int auction,power,year,passengers,available;
        float price, weight;
        for(int i=0; i<dbC.size(); i++){
            int flag=-1;
            nameOI = dbC.get(i).getTitle();
            brand = dbC.get(i).getBrand();
            auction = dbC.get(i).getIs_auction();
            price = dbC.get(i).getPrice();
            weight = dbC.get(i).getWeight();
            account = dbC.get(i).getAccount().getNick();
            available = dbC.get(i).getIs_available();
            power = dbC.get(i).getPower();
            year = dbC.get(i).getYear_of();
            passengers = dbC.get(i).getPassengers();
            transmission = dbC.get(i).getTransmission();
            country = dbC.get(i).getCountry();
            for (int j=0; j<profiles.size(); j++) {
                if(profiles.get(j).getNick().contains(account))
                {
                    flag = j;
                    break;
                }
            }
            if(flag!=-1){
                cars.add(new Car(nameOI,brand,auction,price,weight,profiles.get(flag),available,power,year,passengers,transmission,country));
            }
        }
        return cars;
    }

    /**
     * Loading values from the .txt file
     * @param profiles list of all accounts (type LSEA)
     * @return list with bikes (type Bikes)
     */
    public List<Car> load(List<LSEA> profiles){
        List<Car>cars = new ArrayList<>();
        File file = new File("cars.txt");
        String nameOI, brand,transmission,country,account;
        int auction,power,year,passengers,available,flag=-1;
        float price, weight;
        try {
            Scanner input = new Scanner(file);
            while(input.hasNextLine()) {
                nameOI = input.nextLine();
                brand=input.nextLine();
                auction = Integer.parseInt(input.nextLine());
                price = Float.parseFloat(input.nextLine());
                weight = Float.parseFloat(input.nextLine());
                account = input.nextLine();
                available = Integer.parseInt(input.nextLine());
                power = Integer.parseInt(input.nextLine());
                year = Integer.parseInt(input.nextLine());
                passengers = Integer.parseInt(input.nextLine());
                transmission = input.nextLine();
                country = input.nextLine();
                for (int i=0; i<profiles.size(); i++) {
                    if(profiles.get(i).getNick().contains(account))
                    {
                        flag = i;
                        break;
                    }
                }
                if(flag != -1){
                    //when there is the person that poses this item
                    cars.add(new Car(nameOI,brand,auction,price,weight,profiles.get(flag),available,power,year,passengers,transmission,country));
                }
            }
            input.close();
        }catch(IOException ex){
            System.out.print(ex.getMessage());
        }
        return cars;

    }

    /*** override of the method compare from the Comparator<Car> interface
     * @param o1 Car object
     * @param o2 Another Car object
     * @return result 1/-1 to sort
     */
    @Override
    public int compare(Car o1, Car o2) {
        int result = o1.getNameOI().compareTo(o2.getNameOI());
        if(result == 0) {
            if(o1.getPrice() > o2.getPrice()) result = 1;
            else result = -1;
        }
        return result;
    }

    /*** deep cloning
     * @return car, which is clone of car class object with LSEA addition
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Car car = (Car) super.clone();
        car.account = (LSEA) account.clone();
        return car;
    }

    /*** overriding toString method to print values from the class*/
    @Override
    public String toString() {
        return "Bike [nameOI=" + nameOI + ", brand=" + brand + ", auction=" + is_auction + ", price=" + price + ", weight="
                + weight + ", acc=" + account.getName() + ", available=" + is_available + ", year=" + year + ", power=" + power
                + ", passengers=" + passengers + ", transmission=" + transmission + ", country=" + country + "]";
    }
}
