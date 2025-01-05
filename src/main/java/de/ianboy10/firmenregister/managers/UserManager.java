package de.ianboy10.firmenregister.managers;

import de.ianboy10.firmenregister.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserManager {

    // Überprüfen, ob ein Spieler in einer Firma ist
    public static boolean inCompany(UUID uuid) {
        return Company.isInAnyCompany(uuid, CompanyManager.getAllCompanies());
    }

    // Die Firmen-ID eines Spielers abrufen
    public static String getCompany(UUID uuid) {
        Collection<Company> companies = CompanyManager.getAllCompanies();
        return Company.getPlayerCompany(uuid, companies)
                .map(Company::getCompanyId)
                .orElse(null);
    }

}