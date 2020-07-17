package main.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Session;

import javax.persistence.*;
import javax.transaction.Transaction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class used for DB handling.<br>
 * It contains methods for loading the data to the database from files, deleting or updating data and executing query.<br>
 * All setters and getters are automatically generated by lombok.<br>
 * NoArgsConstructor constructor is also generated automatically.
 *
 * @author Kamil Rutkowski
 * @author Patryk Dunajewski
 * @author Marcin Hebdzynski
 *
 */
@NoArgsConstructor @Getter @Setter
public class DB_Handling {

    private ArrayList<DB_Profile> dbP = new ArrayList<>();
    private ArrayList<DB_Frame> dbF = new ArrayList<>();
    private ArrayList<DB_Car> dbC = new ArrayList<>();
    private ArrayList<DB_Bike> dbB = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        DB_Handling db_H = new DB_Handling();
        EntityManagerFactory eMF = Persistence.createEntityManagerFactory("DataBaseP");
        EntityManager entityManager = eMF.createEntityManager();
        db_H.loadProfiles("data/profiles", entityManager);
        db_H.loadFrames("data/frames", entityManager);
        db_H.loadCars("data/cars", entityManager);
        db_H.loadBikes("data/bikes", entityManager);
        db_H.deleteBikeBrand("Bikos", entityManager);
        db_H.updatePassword("Test",entityManager);
        db_H.query(entityManager, 21, 5000);
        entityManager.close();
        eMF.close();
    }

    /**
     * method used for loading data from file to the database (profiles)
     *
     * @param uPath Path of the file(String)
     * @param eM instance of entity Manager, needed for transactions
     */
    public boolean loadProfiles(String uPath, EntityManager eM){

        String nick, password, name, surname;
        int age;
        float money;

        File file = new File(uPath+".txt");
        if(file.exists()) {
            try (FileChannel fch = FileChannel.open(file.toPath());
                 Scanner input = new Scanner(file)) {
                FileLock lock = fch.tryLock(0L, Long.MAX_VALUE, true);
                if (lock != null) {
                    System.out.println("Locked File " + uPath + ".txt");
                    while (input.hasNextLine()) {
                        nick = input.nextLine();
                        password = input.nextLine();
                        name = input.nextLine();
                        surname = input.nextLine();
                        age = Integer.parseInt(input.nextLine());
                        money = Float.parseFloat(input.nextLine());
                        DB_Profile dbp = new DB_Profile(nick, password, name, surname, age, money);
                        this.getDbP().add(dbp);
                        EntityTransaction eT = eM.getTransaction();
                        eT.begin();
                        eM.persist(dbp);
                        eT.commit();
                    }
                    lock.release();
                }
            } catch (IOException ex) {
                System.out.print(ex.getMessage());
            } finally {
                System.out.println("Released Lock on file " + uPath + ".txt");
                return true;
            }
        }
        else {
            System.out.println("Specified path does not exist - " + uPath + ".txt");
            return false;
        }
    }

    /**
     * method used for loading data from file to the database (frames)
     *
     * @param uPath Path of the file(String)
     * @param eM instance of entity Manager, needed for transactions
     */
    public boolean loadFrames(String uPath, EntityManager eM){

        File file = new File(uPath+".txt");
        if(file.exists()) {
            ReadWriteLock lock = new ReentrantReadWriteLock();
            lock.readLock().lock();
            System.out.println("Locked File " + uPath + ".txt");
            try {
                Scanner input = new Scanner(file);
                while (input.hasNextLine()) {
                    String line = input.nextLine();
                    String[] row = line.split(",");
                    DB_Frame frame = new DB_Frame(Integer.parseInt(row[0]), row[1]);
                    this.getDbF().add(frame);
                    EntityTransaction tx = eM.getTransaction();
                    tx.begin();
                    eM.persist(frame);
                    tx.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.readLock().unlock();
                System.out.println("Released Lock on file " + uPath + ".txt");
                return true;
            }
        }
        else {
            System.out.println("Specified path does not exist - " + uPath + ".txt");
            return false;
        }
    }

    /**
     * method used for loading data from file to the database (cars)
     *
     * @param uPath Path of the file(String)
     * @param eM instance of entity Manager, needed for transactions
     * @throws FileNotFoundException
     */
    public boolean loadCars(String uPath, EntityManager eM) throws FileNotFoundException {

        String title, brand, transmission, country;
        int is_auction, id_account, available, year_of, power, passengers;
        float price, weight;

        File file = new File(uPath+".txt");
        if(file.exists()) {
            try (FileChannel fch = FileChannel.open(file.toPath());
                 Scanner in = new Scanner(file)) {
                FileLock lock = fch.tryLock(0L, Long.MAX_VALUE, true);
                if (lock != null) {
                    System.out.println("Locked File " + uPath + ".txt");
                    while (in.hasNextLine()) {
                        title = in.nextLine();
                        brand = in.nextLine();
                        is_auction = Integer.parseInt(in.nextLine());
                        price = Float.parseFloat(in.nextLine());
                        weight = Float.parseFloat(in.nextLine());
                        id_account = Integer.parseInt(in.nextLine());
                        available = Integer.parseInt(in.nextLine());
                        year_of = Integer.parseInt(in.nextLine());
                        power = Integer.parseInt(in.nextLine());
                        passengers = Integer.parseInt(in.nextLine());
                        transmission = in.nextLine();
                        country = in.nextLine();
                        DB_Profile profile = eM.getReference(DB_Profile.class, id_account);
                        DB_Car car = new DB_Car(title, brand, is_auction, price, weight, profile, available, year_of, power, passengers, transmission, country);
                        this.getDbC().add(car);
                        EntityTransaction tx = eM.getTransaction();
                        tx.begin();
                        eM.persist(car);
                        tx.commit();
                    }
                    lock.release();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Released Lock on file " + uPath + ".txt");
                return true;
            }
        }
        else {
            System.out.println("Specified path does not exist - " + uPath + ".txt");
            return false;
        }
    }

    /**
     * method used for loading data from file to the database (bikes)
     *
     * @param uPath Path of the file(String)
     * @param eM instance of entity Manager, needed for transactions
     */
    public boolean loadBikes(String uPath, EntityManager eM) {

        int auction, available, gear, lights, bell, breaks, id_account, id_frame;
        float weight, price;
        String nameOI, brand;

        File file = new File(uPath+".txt");
        if(file.exists()) {
            ReadWriteLock lock = new ReentrantReadWriteLock();
            lock.readLock().lock();
            System.out.println("Locked File " + uPath + ".txt");
            try {
                Scanner input = new Scanner(file);
                while (input.hasNextLine()) {
                    nameOI = input.nextLine();
                    brand = input.nextLine();
                    auction = Integer.parseInt(input.nextLine());
                    price = Float.parseFloat(input.nextLine());
                    weight = Float.parseFloat(input.nextLine());
                    id_account = Integer.parseInt(input.nextLine());
                    available = Integer.parseInt(input.nextLine());
                    gear = Integer.parseInt(input.nextLine());
                    id_frame = Integer.parseInt(input.nextLine());
                    lights = Integer.parseInt(input.nextLine());
                    bell = Integer.parseInt(input.nextLine());
                    breaks = Integer.parseInt(input.nextLine());
                    DB_Profile profile = eM.getReference(DB_Profile.class, id_account);
                    DB_Frame frame = eM.getReference(DB_Frame.class, id_frame);
                    DB_Bike bike = new DB_Bike(nameOI, brand, auction, price, weight, profile, available, gear, frame, lights, bell, breaks);
                    this.getDbB().add(bike);
                    EntityTransaction tx = eM.getTransaction();
                    tx.begin();
                    eM.persist(bike);
                    tx.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.readLock().unlock();
                System.out.println("Released Lock on file " + uPath + ".txt");
                return true;
            }
        }
        else {
            System.out.println("Specified path does not exist - " + uPath + ".txt");
            return false;
        }
    }

    /**
     * method used for deleting bike by typing its brand<br>
     * it's possible to delete more than one row at one time
     *
     * @param brand name of a brand of the object (protected String)
     * @param eM instance of entity Manager, needed for transactions
     */
    public boolean deleteBikeBrand(String brand, EntityManager eM){

        int flag = 0;
        if(this.getDbB().size() > 0) {
            // remove from database
            for (int i = 1; i <= this.getDbB().size(); i++) {
                DB_Bike bikeCheck = eM.find(DB_Bike.class, i);
                if (bikeCheck != null) {
                    if (bikeCheck.getBrand().equals(brand)) {
                        eM.getTransaction().begin();
                        eM.remove(bikeCheck);
                        eM.getTransaction().commit();
                        System.out.println("Bike " + brand + " was successfully deleted");
                        flag = 1;
                        i -= 1;
                        break;
                    } else if (i == this.getDbB().size()) System.out.println("Such brand does not exist");
                }
            }
            // remove form array list
            for (int i = 0; i <= this.getDbB().size() - 1; i++) {
                if (this.getDbB().get(i).getBrand().equals(brand)) {
                    this.getDbB().remove(i);
                    i -= 1;
                }
            }
        }
        else System.out.println("Unable to delete bike, list of bikes is empty");

        if (flag == 1) return true;
        else return false;
    }

    /**
     * method used for updating password for current user
     *
     * @param newPassword new password, written by user
     * @param eM instance of entity Manager, needed for transactions
     */
    public boolean updatePassword(String newPassword, EntityManager eM){

        DB_Profile dbp = eM.find(DB_Profile.class, this.getDbP().size());
        eM.getTransaction().begin();
        dbp.setPassword(newPassword);
        eM.persist(dbp);
        eM.getTransaction().commit();

        if(this.getDbP().size() > 0) {
            System.out.println("Changing password in progress");
            this.getDbP().get(this.getDbP().size() - 1).setPassword(newPassword);
            return true;
        }
        else{
            System.out.println("Unable to change password, list of profiles is empty");
            return false;
        }
    }

    /**
     * method used for querying the dattabase (using SELECT statement)
     *
     * @param eM instance of entity Manager, needed for transactions
     * @param parameter1 value of age argument, written by user
     * @param parameter2 value of money argument, written by user
     */
    public boolean query(EntityManager eM, int parameter1, float parameter2){
        String queryString = "SELECT p FROM DB_Profile p WHERE p.age > :age AND p.money < :money";
        Query query = eM.createQuery(queryString);
        query.setParameter("age", parameter1);
        query.setParameter("money", parameter2);
        List<DB_Profile> profiles = query.getResultList();
        for(int i=0; i<profiles.size(); i++){
            System.out.println(profiles.get(i).toString());
        }
        return true;
    }
}
