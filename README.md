# Rent A Car Projesi

## Projenin Amacı
  Bu proje, bir araç kiralama platformu oluşturmak için geliştirildi. Kullanıcılar istedikleri araçları filtreleyerek seçebiliyor, fiyat hesaplaması yapabiliyor ve hızlı bir şekilde kiralama işlemi gerçekleştirebiliyor. Adminler ise araç ve kullanıcı yönetimi gibi işlemleri kolaylıkla gerçekleştirebiliyor.

## Kullanıcı İşlevleri
**Giriş ve Kayıt**: Kullanıcılar, sisteme kayıt olarak giriş yapabilir. JWT sistemi sayesinde güvenli oturumlar sağlanır.  
**Araç Listeleme**: Araçları markasına, rengine ve günlük fiyatına göre filtreleyebilirsiniz.  
**Araç Kiralama**: Tarih aralığı seçerek, toplam fiyat bilgisiyle birlikte istediğiniz aracı kiralayabilirsiniz. Kiralama sonrası araç stoktan düşer.  
**Sipariş Geçmişi**: Daha önce kiraladığınız araçların detaylarını bir sayfa üzerinden görebilirsiniz.  
**Otomatik Stok Yönetimi**: Stok 0 olduğunda araç listeden kaldırılır, stok arttığında tekrar görüntülenebilir hale gelir.  
## Admin İşlevleri
**Araç Yönetimi**: Yeni araç ekleyebilir, mevcut araçları güncelleyebilir veya silebilirsiniz. Araçların özelliklerini (görünürlük, renk, fiyat vb.) düzenleyebilirsiniz.  
**Kiralama Geçmişi Görüntüleme**: Bütün kullanıcıların kiralama geçmişine erişerek detaylı inceleme yapabilirsiniz.  
**Araç Teslim Alma İşlemi**: Kullanıcıdan teslim alınan araçların stoğu otomatik güncellenir. Eğer araç pasif durumda ise, teslim alındıktan sonra tekrar görünür hale gelir.  
**Marka Yönetimi**: Bir markanın silinmesi sırasında, bağlı araçlar varsa işlemin engellenmesi gibi kontroller mevcuttur.  

## Teknolojiler
### Backend
*Java (Spring Boot)*
*Java8*
*OOP*
*Maven*
*Java8*
*RESTful API mimarisi*
*JWT (JSON Web Tokens)*: Giriş ve yetkilendirme süreçlerinde güvenliği sağlar.
*Swagger* :  API dokümantasyonu için entegre edildi.

### Frontend
*HTML, CSS, JavaScript* 
### Veritabanı 
*PostgreSQL*  ile veriler güvenli bir şekilde saklanır.


## Öne Çıkan Özellikler
**Stok ve Görünürlük Yönetimi**: Araç stokları otomatik olarak güncellenir. Görünmez araçlar, stok arttığında tekrar aktif hale gelir.  
**Dinamik Fiyat Hesaplama**: Kullanıcı tarih seçimine göre toplam kiralama maliyetini anında görebilir.  
**Responsive Tasarım**: Tüm sayfalar farklı cihazlarda sorunsuz çalışacak şekilde tasarlanmıştır.  
**Rol Tabanlı Yetkilendirme**: Kullanıcı ve admin rolleri arasında ayrım yapılır. Adminler geniş yetkilere sahiptir.  
**Kolay Entegrasyon**: Projeyi Docker ile kolayca başlatabilir, modern yazılım geliştirme süreçlerine entegre edebilirsiniz.  

## Nasıl Çalışır?
### Kullanıcılar İçin
Giriş yaparak araç kiralayabilir, sipariş geçmişini görüntüleyebilir.
Araçları detaylı bir şekilde inceleyip uygun tarih aralığında kiralama işlemi yapabilir.
### Adminler İçin
Araçlar ve kullanıcılar üzerinde tam kontrole sahiptir.
Araç ekleme, düzenleme ve teslim işlemleri gibi yönetimsel işlemleri gerçekleştirebilir.



*ReadMe Düzenleme Kaynağm: https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax*
