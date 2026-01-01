package BankaSistemi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Admin extends Kullanici {
	public Admin(String adSoyad, int sifre, int tcno, float bakiye,float gelir,HashSet<Kredi>krediler,int kredipuani) {
		super(adSoyad, sifre, tcno, bakiye,gelir, null,kredipuani);
		bakiye = 0;
		
		
	}

	public void HesapGoster(ArrayList<Kullanici> musteriler,int secim) {

		for (Kullanici musteri : musteriler) {

			if (secim == 1) {
				if (musteri instanceof Musteri) {
					System.out.println("----------------------------------------------------");
					musteri.HesapGoster();
					System.out.println("----------------------------------------------------");
				}
			} else if (secim == 2) {
				if (musteri instanceof Calisan) {
					System.out.println("----------------------------------------------------");
					musteri.HesapGoster();
					System.out.println("----------------------------------------------------");
				}

			}
			System.out.println();
		}

	}

	public float BankaSermaye(ArrayList<Kullanici> kullanicilar) {
		bakiye = 0;
		for (Kullanici musteri : kullanicilar) {
			if (musteri instanceof Musteri) {
				Musteri temp = (Musteri) musteri;
				bakiye += temp.bakiye;
			}
		}
		return bakiye;
	}

	public float BankaGideri(ArrayList<Kullanici> kullanicilar) {
		float gider = 0;

		for (Kullanici calisan : kullanicilar) {

			if (calisan instanceof Calisan) {
				Calisan temp = (Calisan) calisan;

				gider += temp.gelir;
			}
		}
		return gider;
	}
}
