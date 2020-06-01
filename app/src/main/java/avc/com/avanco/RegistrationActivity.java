package avc.com.avanco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import avc.com.avanco.model.Products;
import avc.com.avanco.model.Users;
import avc.com.avanco.prevalent.Prevalent;

public class RegistrationActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText e1,e2,e3, e4, email;
    Button b1, b2;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        db = new DatabaseHelper(this);
        e1 = (EditText)findViewById(R.id.nomeUsuario);
        e2 = (EditText)findViewById(R.id.senha);
        e3 = (EditText)findViewById(R.id.confirmarSenha);
        e4 = (EditText)findViewById(R.id.phone);
        email = (EditText)findViewById(R.id.email);
        b1 = (Button)findViewById(R.id.botaoRegistro);
        b2 = (Button)findViewById(R.id.botaoLogin);


        loadingBar = new ProgressDialog(this);


        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                CriarConta();

                /*
                String s1 = e1.getText().toString();
                String s2 = e2.getText().toString();
                String s3 = e3.getText().toString();
                if ( s1.equals("") || s2.equals("") || s3.equals("") ) {
                    Toast.makeText(getApplicationContext(), "Favor preencher todos os campos", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (s2.equals(s3)) {
                        Boolean checkEmail = db.checkEmail(s1);
                        if (checkEmail==true) {
                            Boolean insert = db.insert(s1,s2);
                            if (insert==true){
                                Toast.makeText(getApplicationContext(), "Registrado com Sucesso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "E-mail já existente, inserir outro", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"As senhas não combinam", Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        });
        //Redirecionar para login
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

    }

    private void CriarConta() {

        String s1 = e1.getText().toString();
        String s2 = e2.getText().toString();
        String s3 = e3.getText().toString();
        String s4 = email.getText().toString();
        String phone = e4.getText().toString();

        if ( s1.equals("") || s2.equals("") || s3.equals("") || phone.equals("") || s4.equals("") ) {
            Toast.makeText(getApplicationContext(), "Favor preencher todos os campos", Toast.LENGTH_SHORT).show();
        }
        else {
            if (s2.equals(s3)) {
                //  Boolean checkEmail = db.checkEmail(s1);
                // if (checkEmail==true) {
                //    Boolean insert = db.insert(s1,s2);
                /// if (insert==true){
                loadingBar.setTitle("Criar conta");
                loadingBar.setMessage("Por favor aguarde enquanto estamos checando suas credenciais");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                //Se o nomeUsuario for valido, é chamado o método validar numero de telefone.
                ValidateNomeUser(s1,s2,phone, s4 );

                //Toast.makeText(getApplicationContext(), "Registrado com Sucesso", Toast.LENGTH_SHORT).show();
                // }
                // }
                //  else {
                //   Toast.makeText(getApplicationContext(), "E-mail já existente, inserir outro", Toast.LENGTH_SHORT).show();
                //   }
            }
            else {
                Toast.makeText(getApplicationContext(),"As senhas não combinam", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ValidatephoneNumber(final String s1, final String s2, final String phone, final String s4)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists()) || !(dataSnapshot.child("Users").child(phone).child("email").exists()) )
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("nomeUsuario", s1);
                    userdataMap.put("senha", s2);
                    userdataMap.put("email", s4);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegistrationActivity.this , "Parabéns, sua conta foi criada!", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        startActivity(intent);

                                        String fromEmail = "avancotech.app@gmail.com";
                                        String fromPassword = "avancoapp";
                                        String toEmails = s4 ;
                                        List<String> toEmailList = Arrays.asList(toEmails
                                                .split("\\s*,\\s*"));
                                        Log.i("SendMailActivity", "To List: " + toEmailList);
                                        String emailSubject = "AVANÇO APP - Bem vindo, " + s1 + "!";
                                        String emailBody = (

                                                " <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
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
                                                        "                      <td class=\"es-m-txt-l\" align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;\"><h3 style=\"Margin:0;line-height:22px;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;font-size:18px;font-style:normal;font-weight:bold;color:#333333;\">Olá " + s1 +",</h3></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"left\" style=\"padding:0;Margin:0;padding-bottom:10px;padding-top:15px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:16px;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:24px;color:#777777;\">Seja bem-vindo ao Avanco App! Faça suas compras online através de nosso aplicativo de forma rápida e prática.</p></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:15px;padding-top:20px;font-size:0;\"> \n" +
                                                        "                       <table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                                                        "                         <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                          <td style=\"padding:0;Margin:0px;border-bottom:3px solid #EEEEEE;background:#FFFFFFnone repeat scroll 0% 0%;height:1px;width:100%;margin:0px;\"></td> \n" +
                                                        "                         </tr> \n" +
                                                        "                       </table></td> \n" +
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
                                                        "                      <td align=\"left\" style=\"padding:0;Margin:0;\"><h2 style=\"Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;font-size:24px;font-style:normal;font-weight:bold;color:#333333;\">Seus dados cadastrais</h2></td> \n" +
                                                        "                     </tr> \n" +
                                                        "                     <tr style=\"border-collapse:collapse;\"> \n" +
                                                        "                      <td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:16px;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:24px;color:#777777;\">Telefone celular (Login): "+ phone + "<br>Nome de usuário: " + s1 + "<br>E-mail: "+s4+"<br>Senha: "+s2+"</p></td> \n" +
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
                                                        " </body>\n" +
                                                        "</html>"

                                        );




                                        new SendMailTask(RegistrationActivity.this).execute(fromEmail,
                                                fromPassword, toEmailList, emailSubject, emailBody);
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegistrationActivity.this , "Erro na rede, For favor, tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
                else {
                    Toast.makeText(RegistrationActivity.this , "Este número "+ phone + " já existe.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegistrationActivity.this , "Favor, usar outro número de telefone", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void ValidateNomeUser(final String s1, final String s2, final String phone,  final String s4)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(s1).exists()) )
                {
                    ValidatephoneNumber(s1,s2,phone, s4 );


                }
                else {
                    Toast.makeText(RegistrationActivity.this , "Este nome de usuário já existe.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegistrationActivity.this , "Favor, usar outro nome.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}


/*
backup MANIFESTS
    <activity android:name=".LoginActivity"></activity>
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
    */