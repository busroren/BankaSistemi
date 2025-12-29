package BankaSistemi;

import java.util.HashSet;

public abstract class Kullanici implements HesapGoster {
	protected String adSoyad;
	protected int sifre;
	public int tcno;
	public float bakiye;
	public int kredipuani;
	public HashSet<Kredi>krediler;
	public float gelir;
	public Kullanici(String adSoyad, int sifre, int tcno,float bakiye,float gelir,HashSet<Kredi>krediler,int kredipuani) {
		this.bakiye=bakiye;
		this.adSoyad=adSoyad;
		this.sifre = sifre;
		this.tcno = tcno;
		this.krediler=krediler;
		this.kredipuani=kredipuani;
		this.gelir=gelir;
	}
	public String getAdSoyad() {
		return adSoyad;
	}
	public void setAdSoyad(String adSoyad) {
		this.adSoyad=adSoyad;
	}
	public int getSifre() {
		return sifre;
	}
	public void setSifre(int sifre) {
		this.sifre = sifre;
	}

	public void HesapGoster() {
		
	}
	

}
