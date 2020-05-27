package avc.com.avanco.model;

public class AdminOrders {

    private String address,cep,cpf,date,name,phone,status,time, totalPedido;

    public AdminOrders (){

    }

    public AdminOrders(String address, String cep, String cpf, String date, String name, String phone, String status, String time, String totalPedido) {
        this.address = address;
        this.cep = cep;
        this.cpf = cpf;
        this.date = date;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.time = time;
        this.totalPedido = totalPedido;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(String totalPedido) {
        this.totalPedido = totalPedido;
    }
}
