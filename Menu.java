//*****************************************************************************************
//PROJENİN GUI VE SQL BULANAN KISIMLARI HARİÇ HİÇBİR YERDE YAPAYZEKAYA KOD YAZDIRILMAMIŞTIR 
//*****************************************************************************************
package BankaSistemi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Menu {

	public ArrayList<Kullanici> kullanicilar;
	private Admin admin;
    
	public Menu(ArrayList<Kullanici> kullanicilar) {
		this.kullanicilar = new ArrayList<>();
		this.kullanicilar = kullanicilar;
		this.admin = new Admin("PATRON", 12345, 12345, 0,0, null, 0);
		
	}

	public void clear() {
		for (int x = 0; x < 4; x++) {
			System.out.println();
		}
	}

	public void menuarayuz() {
		int sistemdenatilma = 0;
		while (true) {

			if (sistemdenatilma == 1) {
				clear();
				System.out.println("Sistemden otomatik atıldınız  ... İyi günler");
				break;
			}
			clear();
			Scanner i = new Scanner(System.in);
			System.out.println("1->Admin Girisi");
			System.out.println("2->Musteri Girisi");
			System.out.println("->Çıkış için -1 i tuşla");
			System.out.println("->İşlemi seciniz...");
			int x = i.nextInt();

			if (x == 1) {
				clear();
				System.out.println("->Tek hakkınız var yanlış yazarsanız sistemden atılırsınız");
				System.out.println("->Admin kullanici adini giriniz : ");
				int adkull;
				adkull = i.nextInt();
				if (adkull == -1) {
					continue;
				}
				System.out.println("->Admin sifresini giriniz : ");
				int adsifre;
				adsifre = i.nextInt();
				if (adsifre == -1) {
					continue;
				} else if (adsifre == 12345 && adkull == 12345) {
					AdminHesapMenu();

				} else {
					sistemdenatilma = 1;
					continue;
				}

			} else if (x == 2) {
				while (true) {

					int y;
					System.out.println("-----------  Musteri Ekranı  ------------");
					System.out.println("1-> Varolan müşteri girişi");
					System.out.println("2-> Yeni müşteri kayıdı");
					System.out.println("->Seçim yapın");
					y = i.nextInt();
					if (y == 1) {
						int hesapgirisdogrulama = HesapGiris();
						MusteriHesapMenu(hesapgirisdogrulama);
						if (hesapgirisdogrulama == -2) {
							System.out.println("Hesaba giriş yaparken şifre deneme sınırına ulaştınız...");
							sistemdenatilma = 1;
							break;
						}
					} else if (y == 2) {
                      YeniUyeKaydi();
					}else if(y==-1) {
						break;
					}else {
						System.out.println("Geçersiz tuşlama yaptınız");
					}
				}

			} else if (x == -1) {
				clear();
				System.out.println("\n \n \n \n İyi günler...");
				break;
			} else {
				System.out.println("Yanlış tuşlama yaptınız");
			}

		}
	}

	public void YeniUyeKaydi() {
		while (true) {

			Scanner i = new Scanner(System.in);
			clear();

			String ad;
			String soyad;
			int sifre;
			int tc;
			String cinsiyet;
			float gelir;
			int arkadasdavetkodu;
			int davetkodu;
			int musterino;
			float bakiye = 0;
			System.out.println("------- Yeni Üye Kayıdı --------------");
			System.out.println("->Çıkış için herhangi birine -1 yazın");
			System.out.println("->Adınızı girin : ");
			ad = i.nextLine();
			if (ad.equals("-1")) {
				break;
			}
			System.out.println("->Soayadınızı girin : ");
			soyad = i.nextLine();
			if (soyad.equals("-1")) {
				break;
			}
			System.out.println("->TC numarasınısı giriniz :");
			tc = i.nextInt();
			i.nextLine();
			if (tc == -1) {
				break;
			}
			if (Tcsorgulama(tc)) {
				System.out.println("->Bu tc geçersiz bu tc ye sahip başka biri daha var tekrar deneyin !!!");
				continue;
			}
			System.out.println("->Cinsiyetinizi giriniz : ");
			cinsiyet = i.nextLine();
		
			System.out.println("->Aylık gelirinizi giriniz : ");
			gelir = i.nextFloat();
			if (gelir == -1) {
				break;
			}
			System.out.println(
					"->Davet kodunuuz varsa giriniz sizde kazanın arkadaşınızda kazansın (Eğer yoksa -1 tuşlayın) : ");
			arkadasdavetkodu = i.nextInt();
			if (arkadasdavetkodu != -1) {
				boolean davetsorgu = DavetKodSistemi(arkadasdavetkodu);
				if (davetsorgu) {
					System.out.println("->Hem arkadaşın hemde sen 500 bakiye kazandınız +++");
					System.out.println("->Sende arkadaşını davet et daha çok kazan +++");
					bakiye += 500;
				} else {
					System.out.println("->Bu davet kodu geçersiz...");
				}
			}

			int max = 9000;
			int min = 1000;
			String isim = ad + " " + soyad;
			musterino = (int) (Math.random() * (max - min + 1)) + min;
			davetkodu = (int) (Math.random() * (max - min + 1)) + min;
			System.out.println("->Şifrenizi belirleyin");
			sifre = i.nextInt();
			if (sifre == -1) {
				break;
			}
			HashSet<Kredi>kredi=new HashSet<>();
			Musteri yenimusteri = new Musteri(isim, sifre, tc, bakiye, cinsiyet, gelir, davetkodu, musterino,kredi,0);
			kullanicilar.add(yenimusteri);
			System.out.println("->Hesabınız başarıyla oluşturuldu giriş yaparak devam edebilirsiniz +++");
			System.out.println("Hesap numaranız : "+musterino);
			break;

		}
	}

	public boolean DavetKodSistemi(int davetkodu) {
		for (int x = 0; x < kullanicilar.size(); x++) {
			if (kullanicilar.get(x) instanceof Musteri) {
				Musteri temp = (Musteri) kullanicilar.get(x);
				if (temp.davetkod == davetkodu) {
					kullanicilar.get(x).bakiye += 500;
					return true;
				}
			}
		}
		return false;
	}

	public int HesapBul(int hesapno) {
		for (int x = 0; x < kullanicilar.size(); x++) {
			if (kullanicilar.get(x) instanceof Musteri) {
				Musteri temp = (Musteri) kullanicilar.get(x);
				if (temp.musterino == hesapno) {
					return x;
				}
			}
		}
		return -1;
	}

	public boolean Tcsorgulama(int tc) {

		for (Kullanici kullanici : kullanicilar) {
			if (kullanici.tcno == tc) {
				return true;
			}
		}
		return false;
	}

	public int Calisangetir(int tc) {
		for (int index = 0; index < kullanicilar.size(); index++) {
			if (kullanicilar.get(index).tcno == tc) {
				if (kullanicilar.get(index) instanceof Calisan) {
					return index;
				}
			}
		}
		return -1;
	}

	public int HesapGiris() {
		int x = 2;
		while (true) {
			clear();
			Scanner i = new Scanner(System.in);
			System.out.println("Çıkış yapmak için şifre yada hesap numarası kısmında -1 i tuşlayın...");
			System.out.println("->Hesap numarası giriniz : ");
			int hesapno = i.nextInt();
			System.out.println("->Şifre giriniz : ");
			int sifre = i.nextInt();
			int index = HesapBul(hesapno);
			if (hesapno == -1 || sifre == -1) {
				return -1;
			} else if (index == -1) {
				clear();
				System.out.println("Kullanici adi yada sifre yanlış girildi ");
				continue;
			} else {
				Musteri temp = (Musteri) kullanicilar.get(index);
				if (temp.getSifre() == sifre) {
					return index;
				} else if (x == 0) {
					return -2;

				} else {
					clear();
					System.out.println("Sifreyi yanlış girdiniz !!!  Son : " + x + " hakkınız kaldı...");
					x--;
				}

			}

		}
	}

	public void MusteriHesapMenu(int index) {
		if (index != -1 && index != -2) {
			while (true) {
				Musteri temp = (Musteri) kullanicilar.get(index);

				clear();
				System.out
						.println(temp.getAdSoyad() + " - Bakiyeniz : " + temp.bakiye + " - Geliriniz : " + temp.gelir);
				System.out.println("-> DAVET KODUNUZ : "+temp.davetkod+" Bu kod ile arkadaşınız ve siz para kazanabilirsiniz");
				System.out.println("1->Para yatırma ");
				System.out.println("2->Para cekme");
				System.out.println("3->Para gönderme");
				System.out.println("4->Kredi çekme sistemi");
				System.out.println("->Çıkış için -1 i tuşla");
				System.out.println("->İşlemi seciniz...");
				Scanner i = new Scanner(System.in);
				int x = i.nextInt();
				float mebla;
				switch (x) {
				case 1: {

					clear();

					System.out.println("Yatırmak istediğiniz miktarı girin...");
					mebla = i.nextFloat();
					kullanicilar.get(index).bakiye += mebla;
					System.out.println("Başarıyla yüklendi...");
				}
					break;
				case 2: {
					clear();
					while (true) {
						System.out.println("Bakiyeniz : " + temp.bakiye);
						System.out.println("Çekmek istediğiniz miktarı girin....");
						System.out.println("Geri için -1 tuşla");
						mebla = i.nextFloat();
						if (temp.bakiye >= mebla && mebla > 0) {
							kullanicilar.get(index).bakiye -= mebla;
							System.out.println("Başarıyla çekildi");
							break;
						} else if (mebla == -1) {
							break;
						} else {

							System.out.println("Yetersiz bakiye yada geçersiz tuşladınız");
						}
					}

				}
					break;
				case 3: {
					while (true) {
						clear();
						System.out.println("Para göndermek istediğiniz hesabın numarasını yazınız");
						x = i.nextInt();
						int paragonderindx = HesapBul(x);
						if (paragonderindx != -1 && temp.musterino != x) {
							clear();
							System.out.println("Bakiyeniz : " + temp.bakiye);
							System.out.println("Gondermek istediğiniz miktarı girin....");
							System.out.println("Geri için -1 tuşla");
							mebla = i.nextFloat();
							if (temp.bakiye >= mebla && mebla > 0) {
								kullanicilar.get(index).bakiye -= mebla;
								kullanicilar.get(paragonderindx).bakiye += mebla;
								System.out.println("Başarıyla gönderildi - Alıcı : "
										+ kullanicilar.get(paragonderindx).getAdSoyad());
								break;
							} else if (mebla == -1) {
								break;
							} else {

								System.out.println("Yetersiz bakiye yada geçersiz tuşladınız");
							}

						} else if (temp.musterino == x) {
							System.out.println("Kendi hesabına burdan gönderemezsin çakkal !!!");
							break;
						}
					}
				}
					break;
				case 4:
					KrediSistem(index);break;
				case -1:
					return;
				}

			}
		}
	}
	public void KrediSistem(int index) {
		int calisansayi=0;
		int calisanindex;
	
		ArrayList<Integer>calisanlarindex=new ArrayList<>();
		clear();
		for(int x=0;x<kullanicilar.size();x++) {
	            if(kullanicilar.get(x) instanceof Calisan) {
	            	calisansayi+=1;
	            	calisanlarindex.add(x);
	            	
	            }
		}
		if(calisanlarindex.size()==0) {
			System.out.println("Sizinle ilgilenecek çalışan bulunmuyor ....");
			return;
		}
		Random rand=new Random();
        calisanindex=calisanlarindex.get(rand.nextInt(calisansayi));
	   
		
		while(true) {
			
	         Musteri musteri=(Musteri)kullanicilar.get(index);
			System.out.println("------------ KREDI SISTEMI --------------");
			System.out.println(kullanicilar.get(calisanindex).getAdSoyad()+" Sizinle İlgileniyor");
			System.out.println("-> Aylık gelir miktarınız : "+musteri.gelir);
			if(musteri.krediler.size()!=0) {
				System.out.println("\n->Mevcut Kredileriniz : ");
				for(Kredi kredi:musteri.krediler) {
					System.out.println("-----");
			          System.out.println("->Kredi türü    : "+ kredi.kredituru);
			          System.out.println("->Kredi miktarı : "+kredi.miktar);
			          System.out.println("->Vade sayısı   : "+kredi.vadesayi+" AY");
    					System.out.println("-----");
     			}
			}
			System.out.println("->Çekebileceğiniz Krediler  : ");
			System.out.println("\n1->Ev Kredisi  ");
			System.out.println("->Minimum gelir gereksinimi : 20000");
			System.out.println("->Miktarı                   : 100000");
			System.out.println("->Vade sayısı               : 18 AY");
			System.out.println("\n2->Araba Kredisi  ");
			System.out.println("->Minimum gelir gereksinimi : 15000");
			System.out.println("->Miktarı                   : 250000 ");
			System.out.println("->Vade sayısı               : 24 AY");
			System.out.println("\n3->Iş Kredisi  ");
			System.out.println("->Minimum gelir gereksinimi : 18000");
			System.out.println("->Miktarı     : 30000");
			System.out.println("->Vade sayısı : 32 AY");
			System.out.println("\n->İstediğiniz krediyi seçiniz : ");
			Scanner i=new Scanner(System.in);
			int x;
			int teminat;
		    x=i.nextInt();
		    
	        if(x==-1) {
	        	break;
	        }
	        System.out.println("->Kredi çekmek için bir teminatınız varmı varsa 1 yoksa 0 ı tuşlayın  (Ev - Araba vb.)");
	        teminat=i.nextInt();
	        if(teminat==0) {
	        	System.out.println("Teminatınız yoksa kredi çekemezsiniz !!!");
	        	break;
	        }
	        Kredi kredi;
	        if(x==1 && KredisiVarmi(18,index) && GelirYeterlilik(20000,index)) {
	        	kredi=new Kredi(100000, "Ev Kredisi",18);
	        	kullanicilar.get(index).krediler.add(kredi);
	        	kullanicilar.get(index).bakiye+=100000;
	        	kullanicilar.get(calisanindex).kredipuani+=15;
	        	kullanicilar.get(calisanindex).gelir+=1500;
	        	}else if(x==1 && !KredisiVarmi(18,index)) {
	        		System.out.println("\n->Bu krediyi zaten çektiniz tekrar çekemezsiniz...\n");
	        		continue;
	        	}else if(x==1 && !GelirYeterlilik(20000,index)) {
	        		System.out.println("->Bu kredi için yeterli geliriniz bulunmuyor...");
	        		continue;
	        	} else if(x==2 && KredisiVarmi(24,index)&& GelirYeterlilik(15000,index)) {
	        		kredi=new Kredi(250000,"Araba Kredisi",24);
	        		kullanicilar.get(index).krediler.add(kredi);
		        	kullanicilar.get(index).bakiye+=250000;
		        	kullanicilar.get(calisanindex).kredipuani+=10;
		        	kullanicilar.get(calisanindex).gelir+=1000;
	        		

	        	}else if(x==2 && !KredisiVarmi(24,index)) {
	        		System.out.println("\n->Bu krediyi zaten çektiniz tekrar çekemezsiniz...\n");
	        		continue;
	        	}else if(x==2 && !GelirYeterlilik(15000,index)) {
	        		System.out.println("->Bu kredi için yeterli geliriniz bulunmuyor...");
	        		continue;
	        	}else if(x==3 && KredisiVarmi(32,index)&& GelirYeterlilik(18000,index)) {
	        		kredi =new Kredi(300000,"Iş Kredisi",32);
	        		kullanicilar.get(index).krediler.add(kredi);
		        	kullanicilar.get(index).bakiye+=300000;
		        	kullanicilar.get(calisanindex).kredipuani+=25;
		        	kullanicilar.get(calisanindex).gelir+=2500;
	        	}else if(x==3 && !KredisiVarmi(20000,index)) {
	        		System.out.println("\n->Bu krediyi zaten çektiniz tekrar çekemezsiniz...\n");
	        		continue;
	        	}else if(x==3 && !GelirYeterlilik(18000,index)) {
	        		System.out.println("->Bu kredi için yeterli geliriniz bulunmuyor...");
	        		continue;
	        	}else if(x==-1) {
	        		System.out.println("Kredi oluşturma talebiniz iptal edildi ---");
	        		break;
	        	}else {
	        		System.out.println("Geçersiz yada yanlış tuşladınız...");
	        		continue;
	        	}
	        System.out.println("Krediniz başarıyla çekildi +++");
	        break;
	        
			
			
		}
	}
	public boolean GelirYeterlilik(float mingelir,int index) {
	      if(kullanicilar.get(index).gelir>=mingelir) {
	    	  return true;
	      }
	      return false;
	}
	public boolean KredisiVarmi(int vade,int index) {
		
		for(Kredi  kullanicikredileri:kullanicilar.get(index).krediler ) {
			if(kullanicikredileri.vadesayi==vade) {
				return false;
			}
		}
		return true;
	}

	public void CalisanIseAlma() {
		clear();
		while (true) {
			Scanner i = new Scanner(System.in);

			String ad;
			String soyad;
			String isim;
			String cikis = "-1";
			float gelir;
			int tc;
			System.out.println("Çıkış için -1 i tşlayın");
			System.out.println("->Alınacak calışan nın Adı : ");
			ad = i.nextLine();
			System.out.println("->Alınacak çalışanın Soyadı : ");
			soyad = i.nextLine();
			System.out.println("->Alinacak çalışanın TC si : ");
			tc = i.nextInt();
			if (Tcsorgulama(tc) == true) {
				clear();
				System.out.println("Bu tc ye sahip birisi var doğru giriş yapınız...");
				continue;
			}
			System.out.println("->Çalisanın maaşını belirle :  ");
			gelir = i.nextFloat();
			if (cikis.equals(ad) || cikis.equals(soyad) || tc == -1 || gelir == -1) {
				break;
			}
			isim = ad + " " + soyad;
			kullanicilar.add(new Calisan(isim, 123, tc, 0, gelir,null, 0));
			System.out.println("---> Eleman Başarıyla işe alındı +++");
			break;

		}
	}

	public void AdminHesapMenu() {

		Scanner i = new Scanner(System.in);

		while (true) {

			clear();
			System.out.println("-> ADMIN PANELİ - Banka Sermayesi : " + admin.BankaSermaye(kullanicilar));
			System.out.println("1->Musteri Hesaplarını görüntüle... ");
			System.out.println("2->Çalışan Bilgilerini görüntüleme...");
			System.out.println("3->Çalışan İşe alma...");
			System.out.println("4->Çalışan İşten kovma...");
			System.out.println("-> Geri dönmek için -1 tuşlayın");
			System.out.println("->İşlemi seciniz...");
			int x = i.nextInt();
			switch (x) {
			case 1: {
				clear();
				System.out.println("--------  MUSTERI LISTESI  ----------- \n");
				admin.HesapGoster(kullanicilar, 1);
			}
				break;
			case 2: {
				clear();
				System.out.println("--------  CALISAN LISTESI  ----------- \n");
				System.out.println("--->Calisan Maas Giderleri : " + admin.BankaGideri(kullanicilar));
				admin.HesapGoster(kullanicilar, 2);
			}
				break;
			case 3: {
				CalisanIseAlma();
			}
				break;
			case 4: {
				while (true) {
					int tc;
					System.out.println("->İşten çıkaracağınız çalışanın tcsini yazınız : ");
					tc = i.nextInt();
					int indi = Calisangetir(tc);
					if (tc == -1)
						break;
					if (indi == -1) {
						System.out.println("->Yazdığınız tcde bir çalışan bulunmuyor...");
						continue;
					}
					clear();
					kullanicilar.get(indi).HesapGoster();
					System.out.println("->Çalışanı kovmak için 1 i tuşlatın yada -1 ile iptal edin");
					int kovsec;
					kovsec = i.nextInt();
					if (kovsec == -1) {
						break;
					} else if (kovsec == 1) {
						System.out.println(kullanicilar.get(indi).getAdSoyad() + " adlı çalışan işten çıkarıldı ...");
						kullanicilar.remove(indi);
						break;
					}

				}
			}
				break;
			case -1:
				return;

			}
		}
	}

}


