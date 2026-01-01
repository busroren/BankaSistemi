package BankaSistemi;

import java.util.ArrayList;
import java.util.HashSet;

public class BankaTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub 
		
		   HashSet <Kredi>krediler=new HashSet<>();
		   HashSet <Kredi>krediler2=new HashSet<>();

	ArrayList<Kullanici>kullanicilar=new ArrayList<>();
//Ali musteri no =1905 sifre=123
//omer musteri no=1904 sifre=123
Musteri musteri=new Musteri("Ali pala",123,1144,1500,"Erkek",20000,12345,1905,krediler,0);
Musteri musteri2=new Musteri("Namık kemal",123,1234,2000,"Erkek",25000,123,1904,krediler2,0);
Calisan calisan=new Calisan("Mahmut Tuncer",123,1122,100,25000,null,45);
kullanicilar.add(calisan);
kullanicilar.add(musteri2);
kullanicilar.add(musteri);
Menu menu=new Menu(kullanicilar);
menu.menuarayuz();
	}

}
