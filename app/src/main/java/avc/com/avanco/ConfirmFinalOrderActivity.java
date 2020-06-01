package avc.com.avanco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import avc.com.avanco.ViewHolder.CartViewHolder;
import avc.com.avanco.model.Cart;
import avc.com.avanco.prevalent.Prevalent;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText cpfEditText, nameEditText, phoneEditText, addressEditText, cepEditText;
    private Button confirmOrderBtn;
    private String totalPedido = "";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        getSupportActionBar().setTitle("Dados de Compra");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.shipment_cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        totalPedido = getIntent().getStringExtra("Preço Total");
        Toast.makeText(this, "Total Pedido: " + totalPedido, Toast.LENGTH_SHORT).show();

        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        cpfEditText = findViewById(R.id.shipment_cpf);
        nameEditText = findViewById(R.id.shipment_name);
        phoneEditText = findViewById(R.id.shipment_phone_number);
        addressEditText = findViewById(R.id.shipment_address);
        cepEditText = findViewById(R.id.shipment_cep);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                Check();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmFinalOrderActivity.this);
                builder.setMessage("Deseja confirmar o pedido?").setPositiveButton("SIM", dialogClickListener)
                        .setNegativeButton("NÃO", dialogClickListener).show();


            }
        });

    }

    private void Check() {

        if (TextUtils.isEmpty(cpfEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu CPF.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu nome completo.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu celular.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu endereço.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cepEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu CEP.", Toast.LENGTH_SHORT).show();
        }
        else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {

        final String saveCurrentDate, saveCurrentTime;

        Calendar calforDate =  Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat( "MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calforDate.getTime());

        final SimpleDateFormat currentTime = new SimpleDateFormat( "HH:mm:ss a");
        saveCurrentTime = currentTime.format(calforDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        ;

        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("cpf", cpfEditText.getText().toString());
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("email", cepEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("status", "Aguardando Pagamento");
        ordersMap.put("totalPedido", totalPedido);

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){


                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        //userInfoDisplay(nameEditText,phoneEditText, addressEditText, cepEditText);

                                        String fromEmail = "avancotech.app@gmail.com";
                                        String fromPassword = "avancoapp";
                                        String toEmails = cepEditText.getText().toString() ;
                                        String nameEditText1  = nameEditText.getText().toString();
                                        String currentDate1 = currentDate.toString();
                                        String currentTime1 = currentTime.toString();
                                        String addressEditText1 = addressEditText.getText().toString();
                                        List<String> toEmailList = Arrays.asList(toEmails
                                                .split("\\s*,\\s*"));
                                        Log.i("SendMailActivity", "To List: " + toEmailList);
                                        String emailSubject = "AVANÇO APP - Confirmação de Pedido";
                                        String emailBody = (


                                                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                                                        "<html style=\"width:100%;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;\">\n" +
                                                        " <head> \n" +
                                                        "  <meta charset=\"UTF-8\"> \n" +
                                                        "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"> \n" +
                                                        "  <meta name=\"x-apple-disable-message-reformatting\"> \n" +
                                                        "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n" +
                                                        "  <meta content=\"telephone=no\" name=\"format-detection\"> \n" +
                                                        "  <title>Novo modelo de e-mail 2020-05-31</title> \n" +
                                                        "  <!--[if (mso 16)]>\n" +
                                                        "    <style type=\"text/css\">\n" +
                                                        "    a {text-decoration: none;}\n" +
                                                        "    </style>\n" +
                                                        "    <![endif]--> \n" +
                                                        "  <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> \n" +
                                                        "  <!--[if !mso]><!-- --> \n" +
                                                        "  <link href=\"https://fonts.googleapis.com/css?family=Open+Sans:400,400i,700,700i\" rel=\"stylesheet\"> \n" +
                                                        "  <!--<![endif]--> \n" +
                                                        "  <style type=\"text/css\">\n" +
                                                        "@media only screen and (max-width:600px) {p, ul li, ol li, a { font-size:16px!important; line-height:150%!important } h1 { font-size:32px!important; text-align:center; line-height:120%!important } h2 { font-size:26px!important; text-align:center; line-height:120%!important } h3 { font-size:20px!important; text-align:center; line-height:120%!important } h1 a { font-size:32px!important } h2 a { font-size:26px!important } h3 a { font-size:20px!important } .es-menu td a { font-size:16px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:16px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:inline-block!important } a.es-button { font-size:16px!important; display:inline-block!important; border-width:15px 30px 15px 30px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } .es-desk-menu-hidden { display:table-cell!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }\n" +
                                                        "#outlook a {\n" +
                                                        "\tpadding:0;\n" +
                                                        "}\n" +
                                                        ".ExternalClass {\n" +
                                                        "\twidth:100%;\n" +
                                                        "}\n" +
                                                        ".ExternalClass,\n" +
                                                        ".ExternalClass p,\n" +
                                                        ".ExternalClass span,\n" +
                                                        ".ExternalClass font,\n" +
                                                        ".ExternalClass td,\n" +
                                                        ".ExternalClass div {\n" +
                                                        "\tline-height:100%;\n" +
                                                        "}\n" +
                                                        ".es-button {\n" +
                                                        "\tmso-style-priority:100!important;\n" +
                                                        "\ttext-decoration:none!important;\n" +
                                                        "}\n" +
                                                        "a[x-apple-data-detectors] {\n" +
                                                        "\tcolor:inherit!important;\n" +
                                                        "\ttext-decoration:none!important;\n" +
                                                        "\tfont-size:inherit!important;\n" +
                                                        "\tfont-family:inherit!important;\n" +
                                                        "\tfont-weight:inherit!important;\n" +
                                                        "\tline-height:inherit!important;\n" +
                                                        "}\n" +
                                                        ".es-desk-hidden {\n" +
                                                        "\tdisplay:none;\n" +
                                                        "\tfloat:left;\n" +
                                                        "\toverflow:hidden;\n" +
                                                        "\twidth:0;\n" +
                                                        "\tmax-height:0;\n" +
                                                        "\tline-height:0;\n" +
                                                        "\tmso-hide:all;\n" +
                                                        "}\n" +
                                                        "</style> \n" +
                                                        " </head> \n" +
                                                        " <body style=\"width:100%;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;\"> \n" +
                                                        "  <div class=\"es-wrapper-color\" style=\"background-color:#EEEEEE;\"> \n" +
                                                        "   <!--[if gte mso 9]>\n" +
                                                        "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                                                        "\t\t\t\t<v:fill type=\"tile\" color=\"#eeeeee\"></v:fill>\n" +
                                                        "\t\t\t</v:background>\n" +
                                                        "\t\t<![endif]--> \n" +
                                                        "   <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;\"> \n" +
                                                        "     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "      <td valign=\"top\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"></tr> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n" +
                                                        "             <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "              <td align=\"left\" style=\"Margin:0;padding-left:10px;padding-right:10px;padding-top:15px;padding-bottom:15px;\"> \n" +
                                                        "               <!--[if mso]><table width=\"580\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"282\" valign=\"top\"><![endif]--> \n" +
                                                        "               <table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"282\" align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"center\" style=\"padding:0;Margin:0;display:none;\"></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table> \n" +
                                                        "               <!--[if mso]></td><td width=\"20\"></td><td width=\"278\" valign=\"top\"><![endif]--> \n" +
                                                        "               <table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"278\" align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"center\" style=\"padding:0;Margin:0;display:none;\"></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table> \n" +
                                                        "               <!--[if mso]></td></tr></table><![endif]--></td> \n" +
                                                        "             </tr> \n" +
                                                        "           </table></td> \n" +
                                                        "         </tr> \n" +
                                                        "       </table> \n" +
                                                        "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"></tr> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "           <table class=\"es-header-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#044767;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#044767\" align=\"center\"> \n" +
                                                        "             <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "              <td align=\"left\" bgcolor=\"#8cb43a\" style=\"Margin:0;padding-top:35px;padding-bottom:35px;padding-left:35px;padding-right:35px;background-color:#8CB43A;\"> \n" +
                                                        "               <!--[if mso]><table width=\"530\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"348\" valign=\"top\"><![endif]--> \n" +
                                                        "               <table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td class=\"es-m-p0r\" width=\"348\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td class=\"es-m-txt-c\" align=\"left\" style=\"padding:0;Margin:0;\"><h1 style=\"Margin:0;line-height:36px;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;font-size:36px;font-style:normal;font-weight:bold;color:#FFFFFF;\">AVANÇO APP!</h1></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table> \n" +
                                                        "               <!--[if mso]></td><td width=\"10\"></td><td width=\"172\" valign=\"top\"><![endif]--> \n" +
                                                        "               <table cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"172\" align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-image:url(https://iitpcn.stripocdn.email/content/guids/ce37f66f-39bc-46b1-885b-cf4fc55c6f19/images/32981590976220251.jpg);background-repeat:no-repeat;background-position:left top;\" background=\"https://iitpcn.stripocdn.email/content/guids/ce37f66f-39bc-46b1-885b-cf4fc55c6f19/images/32981590976220251.jpg\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td class=\"es-m-txt-c\" align=\"center\" height=\"128\" style=\"padding:0;Margin:0;\"></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td style=\"padding:0;Margin:0;\"> \n" +
                                                        "                       <table cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                          <td align=\"center\" style=\"padding:0;Margin:0;display:none;\"></td> \n" +
                                                        "                         </tr> \n" +
                                                        "                       </table></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table> \n" +
                                                        "               <!--[if mso]></td></tr></table><![endif]--></td> \n" +
                                                        "             </tr> \n" +
                                                        "           </table></td> \n" +
                                                        "         </tr> \n" +
                                                        "       </table> \n" +
                                                        "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "           <table class=\"es-content-body\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;\"> \n" +
                                                        "             <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "              <td align=\"left\" style=\"padding:0;Margin:0;padding-left:35px;padding-right:35px;padding-top:40px;\"> \n" +
                                                        "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"530\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td class=\"es-m-txt-l\" align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;\"><h3 style=\"Margin:0;line-height:22px;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;font-size:18px;font-style:normal;font-weight:bold;color:#333333;\">Olá "+nameEditText1+ ",</h3></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"left\" style=\"padding:0;Margin:0;padding-bottom:10px;padding-top:15px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:16px;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:24px;color:#777777;\">Obrigado por comprar conosco!&nbsp;Segue em anexo o boleto bancário para realizar o pagamento.</p></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"left\" style=\"padding:0;Margin:0;\"><h2 style=\"Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;font-size:24px;font-style:normal;font-weight:bold;color:#333333;\"><br></h2></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table></td> \n" +
                                                        "             </tr> \n" +
                                                        "             <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "              <td align=\"left\" style=\"Margin:0;padding-top:30px;padding-bottom:35px;padding-left:35px;padding-right:35px;\"> \n" +
                                                        "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"530\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"left\" style=\"padding:0;Margin:0;\"><h2 style=\"Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;font-size:24px;font-style:normal;font-weight:bold;color:#333333;\">Resumo do Pedido:</h2></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:16px;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:24px;color:#777777;\">PREÇO TOTAL: R$ "+totalPedido+"&nbsp;<br>ENDEREÇO DE ENTREGA: "+addressEditText1+"&nbsp;<br>STATUS: Aguardando Pagamento<br></p></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table></td> \n" +
                                                        "             </tr> \n" +
                                                        "           </table></td> \n" +
                                                        "         </tr> \n" +
                                                        "       </table> \n" +
                                                        "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top;\"> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "           <table class=\"es-footer-body\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;\"> \n" +
                                                        "             <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "              <td align=\"left\" style=\"Margin:0;padding-top:35px;padding-left:35px;padding-right:35px;padding-bottom:40px;\"> \n" +
                                                        "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"530\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:35px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#333333;\"><strong>Equipe AVANÇO TECH!</strong></p></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td esdev-links-color=\"#777777\" align=\"center\" class=\"es-m-txt-c\" style=\"padding:0;Margin:0;padding-bottom:5px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#777777;\">Estamos a disposição!</p></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table></td> \n" +
                                                        "             </tr> \n" +
                                                        "           </table></td> \n" +
                                                        "         </tr> \n" +
                                                        "       </table> \n" +
                                                        "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "           <table class=\"es-content-body\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;\"> \n" +
                                                        "             <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "              <td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;padding-left:35px;padding-right:35px;\"> \n" +
                                                        "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"530\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0;\"><img src=\"https://iitpcn.stripocdn.email/content/guids/CABINET_75694a6fc3c4633b3ee8e3c750851c02/images/18501522065897895.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;\" width=\"46\"></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table></td> \n" +
                                                        "             </tr> \n" +
                                                        "           </table></td> \n" +
                                                        "         </tr> \n" +
                                                        "       </table> \n" +
                                                        "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#1B9BA3;border-bottom:10px solid #48AFB5;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#1b9ba3\" align=\"center\"> \n" +
                                                        "             <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "              <td align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td style=\"padding:0;Margin:0;\"> \n" +
                                                        "                       <table class=\"es-menu\" width=\"40%\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                         <tr class=\"links\" style=\"border-collapse:collapse;\"> \n" +
                                                        "                          <td style=\"Margin:0;padding-left:5px;padding-right:5px;padding-top:35px;padding-bottom:30px;border:0;\" id=\"esd-menu-id-0\" width=\"100%\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;font-size:20px;text-decoration:none;display:block;color:#FFFFFF;\" href=\"https://www.facebook.com/\"></a></td> \n" +
                                                        "                         </tr> \n" +
                                                        "                       </table></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table></td> \n" +
                                                        "             </tr> \n" +
                                                        "           </table></td> \n" +
                                                        "         </tr> \n" +
                                                        "       </table> \n" +
                                                        "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                                                        "         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n" +
                                                        "             <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "              <td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:30px;padding-bottom:30px;\"> \n" +
                                                        "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                 <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                  <td width=\"560\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                                                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"center\" style=\"padding:0;Margin:0;display:none;\"></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                   </table></td> \n" +
                                                        "                 </tr> \n" +
                                                        "               </table></td> \n" +
                                                        "             </tr> \n" +
                                                        "           </table></td> \n" +
                                                        "         </tr> \n" +
                                                        "       </table></td> \n" +
                                                        "     </tr> \n" +
                                                        "   </table> \n" +
                                                        "  </div>  \n" +
                                                        " </body> </html>"


                                        );

                                        try {
                                            new SendMailTask(ConfirmFinalOrderActivity.this).execute(fromEmail,
                                                    fromPassword, toEmailList, emailSubject, emailBody);
                                        }catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(ConfirmFinalOrderActivity.this, "Email incorreto.", Toast.LENGTH_LONG).show();
                                        }



                                        // Toast.makeText(ConfirmFinalOrderActivity.this, "Seu pedido foi processado com sucesso.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, FinishPedidoActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class)
                        .build();

        final FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.txtProductQuantity.setText("Quantidade: " + model.getQuantity());
                //int subtotal = model.getQuantity() * model.getPrice();
                holder.txtProductPrice.setText(" R$ " + model.getPrice());
                holder.txtProductName.setText(model.getPname().toUpperCase());

                //oneTypeProductType = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                //overTotalPrice = overTotalPrice + oneTypeProductType;
                //txtTotalAmount.setText("TOTAL: R$ "+ String.valueOf(overTotalPrice));


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void userInfoDisplay(final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText, final EditText emailEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("email").exists())
                    {
                        String name = dataSnapshot.child("nomeUsuario").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();


                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                        emailEditText.setText(email);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}