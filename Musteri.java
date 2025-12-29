package BankaSistemi;

import java.util.HashSet;

public class Musteri extends Kullanici implements HesapGoster {
	public String cinsiyet;
	
	public int davetkod;
	public int musterino;
	public Musteri(String adSoyad, int sifre, int tcno, float bakiye, String cinsiyet, float gelir,int davetkod,int musterino,HashSet<Kredi>krediler,int kredipuani) {
		super(adSoyad, sifre, tcno, bakiye,gelir,krediler,kredipuani);
		krediler = new HashSet<>();
		this.cinsiyet = cinsiyet;
		this.davetkod=davetkod;
		this.musterino=musterino;
	}
	public void HesapGoster() {
		System.out.println("Adı Soyadı : "+ getAdSoyad());
		System.out.println("Bakiye : "+ bakiye);
		System.out.println("Şifre : " + getSifre());
		System.out.println("TC numarası : " + tcno);
		System.out.println("Gelir miktarı : " + gelir);
		System.out.println("Cinsiyet : " + cinsiyet);
		System.out.println("Müşteri numarası : "+ musterino);

	}
	

}
