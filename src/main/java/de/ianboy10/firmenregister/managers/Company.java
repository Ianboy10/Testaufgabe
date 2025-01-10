package de.ianboy10.firmenregister.managers;

import java.util.*;

public class Company {

    private final String companyId;
    private String name;
    private UUID owner;
    private String description;
    private String bankingId;
    private String biz;
    private final List<UUID> members;

    public Company(String companyId, String name, UUID owner, String description, String bankingId, String biz, List<UUID> members) {
        this.companyId = companyId;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.bankingId = bankingId;
        this.biz = biz;
        this.members = members;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBankingId() {
        return bankingId;
    }

    public void setBankingId(String bankingId) {
        this.bankingId = bankingId;
    }

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID member) {
        this.members.add(member);
    }

    public void removeMember(UUID member) {
        this.members.remove(member);
    }

    public boolean isMember(UUID player) {
        return members.contains(player);
    }

    // Statische Methode: Findet die Firma, in der ein Spieler Mitglied ist.
    public static Optional<Company> getPlayerCompany(UUID player, Collection<Company> allCompanies) {
        return allCompanies.stream().filter(company -> company.isMember(player)).findFirst();
    }

    // Statische Methode: Überprüft, ob ein Spieler in irgendeiner Firma ist.
    public static boolean isInAnyCompany(UUID player, Collection<Company> allCompanies) {
        return getPlayerCompany(player, allCompanies).isPresent();
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId='" + companyId + '\'' +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", description='" + description + '\'' +
                ", bankingId='" + bankingId + '\'' +
                ", biz='" + biz + '\'' +
                ", members=" + members +
                '}';
    }

}
