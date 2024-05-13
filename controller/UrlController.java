package in.co.codeplanet.urlshortner.controller;

import in.co.codeplanet.urlshortner.bean.EmailDetails;
import in.co.codeplanet.urlshortner.bean.User;
import in.co.codeplanet.urlshortner.service.EmailService;
import in.co.codeplanet.urlshortner.utility.Otp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.HashMap;

@RestController
public class UrlController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private JdbcTemplate jdbc;

    @PostMapping("register")
    public String signUp(@RequestBody User user) {

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/url_manager", "root", "root");) {
            String query1 = "select * from user where username=? or email=?";
            PreparedStatement stmt = con.prepareStatement(query1);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == true) {
                return "this username or email already exists";
            } else {
                int otp = Integer.parseInt(Otp.generateOtp(4));
                EmailDetails emailDetails = new EmailDetails(user.getEmail(), "otp verification", "your otp is" + otp);
                emailService.sendMail(emailDetails);

                String query = "insert into user(username,email,password,otp,is_verified) values(?,?,?,?,?)";
                PreparedStatement stmt1 = con.prepareStatement(query);
                stmt1.setString(1, user.getUserName());
                stmt1.setString(2, user.getEmail());
                stmt1.setString(3, user.getPassword());
                stmt1.setInt(4, otp);
                stmt1.setInt(5, 0);
                int result = stmt1.executeUpdate();
                return "your profile has been created successfully";
            }
        } catch (Exception e) {
            return "something went wrong";
        }
    }

    @PostMapping("verification")
    public String emailverification(@RequestBody User user) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/url_manager", "root", "root");) {
            String query = "select otp from user where email=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, user.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == true) {
                if (rs.getInt(1) == user.getOtp()) {
                    String query1 = "update user set is_verified=1 where email=?";
                    PreparedStatement stmt1 = con.prepareStatement(query1);
                    stmt1.setString(1, user.getEmail());
                    stmt1.executeUpdate();
                    return "your account has been successfully verified";
                } else
                    return "otp didnt match kindly try again";
            } else
                return "there is no account corresponding to this email";

        } catch (Exception e) {
            return "something went wrong";
        }

    }

    @PostMapping("login")
    public String signIn(@RequestBody User user) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/url_manager", "root", "root");) {
            String query1 = "select * from user where email=? and password=? and is_verified=1";
            PreparedStatement stmt = con.prepareStatement(query1);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == true) {
                return "login successful";
            } else
                return "either invalid email,password or your account is not verified";
        } catch (Exception e) {
            return "something went wrong";
        }
    }

    @GetMapping("forgotpassword")
    public String forgetPassword(@RequestParam String userName) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/url_manager", "root", "root");) {
            String query = "select email from user where username=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == true) {
                String email = rs.getString(1);
                String password = Otp.generateOtp(8);
                EmailDetails emailDetails = new EmailDetails(email, "new password", "your new password is" + password);
                emailService.sendMail(emailDetails);

                String query1 = "update user set password=? where username=?";
                PreparedStatement stmt1 = con.prepareStatement(query1);
                stmt1.setString(1, password);
                stmt1.setString(2, userName);
                stmt1.executeUpdate();
                return "your new password has been successfully sent over your mai id";
            } else
                return "username doesnt exists";

        } catch (Exception e) {
            return "something went wrong";
        }
    }

    @PostMapping("passwordchange")
    public String passwordChange(@RequestBody User user) {

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/url_manager", "root", "root");) {
            String query = "select * from user where email=? and password=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == true) {
                String query1 = "update user set password=? where email=?";
                PreparedStatement stmt1 = con.prepareStatement(query1);
                stmt1.setString(1, user.getNewPassword());
                stmt1.setString(2, user.getEmail());
                stmt1.executeUpdate();
                return "your password has been updated successfully";
            } else
                return "either email or old password is wrong ";

        } catch (Exception e) {
            return "something went  wrong";
        }
    }

    @GetMapping("urlshortner")
    public String urlShortner(@RequestParam String longUrl, String shortUrl, Integer  userId) {

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/url_manager", "root", "root");) {
            String query1= "select * from url where short_url=?";
            PreparedStatement stmt1 = con.prepareStatement(query1);
            stmt1.setString(1,"cpt.cc/"+shortUrl);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next() == true)
                return "that custom hash is already in use";
            else {
                if (userId == null)
                    userId = 0;
                String query = "insert into url values(?,?,?)";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, longUrl);
                stmt.setString(2, "cpt.cc/"+shortUrl);
                stmt.setInt(3, userId);
                stmt.executeUpdate();
                return "short url succesfully generated";
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return "something went  wrong";
        }
    }
    @GetMapping("longurl")
    public String longUrl(@RequestParam String shortUrl) {

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/url_manager", "root", "root");) {
            String query1= "select long_url from url where short_url=?";
            PreparedStatement stmt1 = con.prepareStatement(query1);
            stmt1.setString(1,"cpt.cc/"+shortUrl);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next() == true)
                return rs.getString(1);
            else {

                return "the short url not linked with any long url";
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return "something went  wrong";
        }
    }
    @GetMapping("allurl")
    public HashMap<String, String> longUrl(@RequestParam int userId) {
        try (Connection con = jdbc.getDataSource().getConnection()){
            String query1="select long_url,short_url from url where user_id=?";
            PreparedStatement stmt1=con.prepareStatement(query1);
            stmt1.setInt(1,userId);
            ResultSet rs=stmt1.executeQuery();
            HashMap<String,String> hm=new HashMap<String,String>();
            while(rs.next()){
                hm.put(rs.getString(2),rs.getString(1));
            }
                return hm;

        }
        catch (Exception exception){
//                exception.printStackTrace();
            return null;

        }
    }
}
