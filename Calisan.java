package BankaSistemi;

import java.util.HashSet;

public class Calisan extends Kullanici implements HesapGoster {
	
	
   public Calisan(String adSoyad, int sifre, int tcno, float bakiye, float gelir, HashSet<Kredi>kredilerint,int kredipuani) {
		super(adSoyad, sifre, tcno, bakiye,gelir,null,kredipuani);
	
	}

   public void HesapGoster() {
      System.out.println("Adı Soyadı: "+ getAdSoyad());
      System.out.println("TC numarası : " + tcno);
      System.out.println("Gelir miktarı : " + gelir);
      System.out.println("Kredi Puanı : " + kredipuani);
   }
}

