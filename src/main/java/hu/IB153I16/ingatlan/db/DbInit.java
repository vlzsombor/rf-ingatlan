package hu.IB153I16.ingatlan.db;

import hu.IB153I16.ingatlan.repository.RealEstateRepository;
import hu.IB153I16.ingatlan.repository.UserRepository;
import hu.IB153I16.ingatlan.model.RealEstate;
import hu.IB153I16.ingatlan.model.Role;
import hu.IB153I16.ingatlan.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;

@Service
public class DbInit implements CommandLineRunner {

    private UserRepository userRepository;
    @Autowired
    private RealEstateRepository realEstateRepository;


    private PasswordEncoder passwordEncoder;

    public DbInit(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Delete all
       // this.userRepository.deleteAll();

        // Crete users
        //User dan = new User("dan",passwordEncoder.encode("dan123"),"USER","");
        //User admin = new User("admin",passwordEncoder.encode("admin123"),"ADMIN","ACCESS_TEST1,ACCESS_TEST2");
        //User manager = new User("manager",passwordEncoder.encode("manager123"),"MANAGER","ACCESS_TEST1");

        // public User(String firstName, String lastName, String email, String password, Collection<Role> roles) {
        //
        User dan = new User("dan","dan","dan@a.hu",passwordEncoder.encode("dan123"),Arrays.asList(new Role("ROLE_USER")));
        //User manager = new User("manager","manager","manager@a.hu",passwordEncoder.encode("manager123"),Arrays.asList(new Role("ROLE_MANAGER")));
        User admin = new User("admin","admin","admin@a.hu",passwordEncoder.encode("admin123"),Arrays.asList(new Role("ROLE_ADMIN")));
        //public RealEstate(String name, String userName, String address, String description, long price)
        //public RealEstate(String name, User user, String address, String description, Long price) {
        RealEstate re = new RealEstate(1L,"Jo nyaralo", dan ,"Elado", "Haz","Ikerhaz", 12345L,55L, 2L ,1L,"Csongrad",6600L, "Szentes", "Somogyi Bela u", 12L, "Szep takaros haz");
        RealEstate re1 = new RealEstate(2L,"Szep nyaralo", dan ,"Kiado", "Lakas","Epitesi telek",12345L,120L,   4L,1L, "Csongrad",6600L, "Szentes", "Somogyi Bela u", 12L, "Szep takaros haz");
        RealEstate re2 = new RealEstate(2L,"Szep haz", dan ,"Kiado", "Lakas","Kamion parkolo",12345L,72L,3L,1L,"Csongrad",6600L, "Szentes", "Somogyi Bela u", 12L, "Szep takaros haz");

        List<User> users = Arrays.asList(dan,admin);
        List<RealEstate> realEstates = Arrays.asList(re,re1,re2);

        // Save to db
        this.userRepository.saveAll(users);
        this.realEstateRepository.saveAll(realEstates);
    }
}
