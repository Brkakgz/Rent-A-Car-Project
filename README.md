# Rent A Car Projesi

## Projenin Amacı
  Bu proje, bir araç kiralama platformu oluşturmak için geliştirildi. Kullanıcılar istedikleri araçları filtreleyerek seçebiliyor, fiyat hesaplaması yapabiliyor ve hızlı bir şekilde kiralama işlemi gerçekleştirebiliyor. Adminler ise araç ve kullanıcı yönetimi gibi işlemleri kolaylıkla gerçekleştirebiliyor.

## Kullanıcı İşlevleri
**Giriş ve Kayıt**: Kullanıcılar, sisteme kayıt olarak giriş yapabilir. JWT sistemi sayesinde güvenli oturumlar sağlanır.  
**Araç Listeleme**: Araçları marka, model gibi özelliklerine göre filtreleyebilirsiniz.  
**Araç Kiralama**: Tarih aralığı seçerek, toplam fiyat bilgisiyle birlikte istediğiniz aracı kiralayabilirsiniz. Kiralama sonrası araç stoktan düşer.  
**Sipariş Geçmişi**: Daha önce kiraladığınız araçların detaylarını bir sayfa üzerinden görebilirsiniz.  
**Otomatik Stok Yönetimi**: Stok 0 olduğunda araç listeden kaldırılır, stok arttığında tekrar görüntülenebilir hale gelir. 
**Şehir Bazlı Kiralama**: Araçları şehir bazında kiralayıp kiraladığınız aracı farklı şehirlerde teslim edebilirsiniz. 
## Admin İşlevleri
**Araç Yönetimi**: Yeni araç ekleyebilir, mevcut araçları güncelleyebilir veya aktifliğini değiştirebilirsiniz. Araçların özelliklerini (görünürlük, renk, fiyat vb.) düzenleyebilirsiniz.  
**Kiralama Geçmişi Görüntüleme**: Bütün kullanıcıların kiralama geçmişine erişerek detaylı inceleme yapabilirsiniz.  
**Araç Teslim Alma İşlemi**: Kullanıcıdan teslim alınan araçların stoğu otomatik güncellenir. Eğer araç pasif durumda ise, teslim alındıktan sonra tekrar görünür hale gelir. Kullanıcının teslim ettiği şehirde araç stoğu otomatik olarak artar. Daha önce araç orada yoksa otomatik olarak stoklara eklenir. 


## Teknolojiler
### Backend
*Java*  
*Spring Framework*  
*Hibernate*  
*JSON*  
*DTO*  
*Java8*  
*OOP*  
*Maven*  
*RESTful API mimarisi*  
*JWT (JSON Web Tokens)*: Giriş ve yetkilendirme süreçlerinde güvenliği sağlar.  
*Swagger* :  API dokümantasyonu için entegre edildi.  

### Frontend
*HTML*  
*CSS*   
*Javascript*
### Veritabanı 
*PostgreSQL*  ile veriler güvenli bir şekilde saklanır.


## Öne Çıkan Özellikler
**Stok ve Görünürlük Yönetimi**: Araç stokları otomatik olarak güncellenir. Görünmez araçlar, stok arttığında tekrar aktif hale gelir.  
**Dinamik Fiyat Hesaplama**: Kullanıcı tarih seçimine göre toplam kiralama maliyetini anında görebilir.  
**Responsive Tasarım**: Tüm sayfalar farklı cihazlarda sorunsuz çalışacak şekilde tasarlanmıştır.  
**Rol Tabanlı Yetkilendirme**: Kullanıcı ve admin rolleri arasında ayrım yapılır. Adminler geniş yetkilere sahiptir.  

## Nasıl Çalışır?
### Kullanıcılar İçin
Giriş yaparak araç kiralayabilir, sipariş geçmişini görüntüleyebilir.
Araçları detaylı bir şekilde inceleyip uygun tarih aralığında kiralama işlemi yapabilir.
### Adminler İçin
Araçlar ve kullanıcılar üzerinde tam kontrole sahiptir.
Araç ekleme, düzenleme ve teslim işlemleri gibi yönetimsel işlemleri gerçekleştirebilir.

## Görseller

**Anasayfa**
![HomePage](https://github.com/user-attachments/assets/697225d0-76f8-49c2-934f-901ea752718a)

**Şehirlere Göre Arama**
![HomePageCity](https://github.com/user-attachments/assets/a1e120d6-9f10-408e-9396-79c33ac0429d)

**Filtreleme**
![HomePageFilter](https://github.com/user-attachments/assets/d1ba2c09-35e7-4133-afb9-10ecbb6a3fbd)

**Kullanıcı Girişi**
![LoginPageUserLogin](https://github.com/user-attachments/assets/c45f3b69-4b2f-4982-bd2f-5fbab75db67c)

**Kullanıcı Kayıt Sayfası**
![RegisterPage](https://github.com/user-attachments/assets/ed703a48-af33-41be-bfc7-d319be2d0c83)

**Kullanıcı Profil Sayfası**
![ProfilePage](https://github.com/user-attachments/assets/f5934276-48e0-4543-9d5d-1c575eb77811)

**Admin Paneli Kiralanmış Araçlar ve Teslim Alma**
![AdminPanelRentedCars](https://github.com/user-attachments/assets/27738c3f-1676-4a39-a232-21fd704333e2)

**Admin Paneli Kiralanma Geçmişi**
![AdminPanelRentalHistory](https://github.com/user-attachments/assets/e54866da-42f5-47cc-9d11-084399f59895)

**Admin Paneli Araç Yönetimi**
![AdminPanelAllCars](https://github.com/user-attachments/assets/81acf41d-3947-4e2b-9b70-8e365e8281b2)




*ReadMe Düzenleme Kaynağm: https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax*
