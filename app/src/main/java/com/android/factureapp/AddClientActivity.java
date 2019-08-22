package com.android.factureapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.factureapp.beans.Client;
import com.android.factureapp.beans.Product;
import com.android.factureapp.db.ClientController.TableControllerClient;
import com.android.factureapp.db.ProductController.TableControllerProduct;

public class AddClientActivity extends AppCompatActivity {

    EditText edt_name, edt_company, edt_town, edt_email, edt_phone, edt_address;
    Button btn_save;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        edt_name = findViewById(R.id.edt_clientname);
        edt_address = findViewById(R.id.edt_address);
        edt_company = findViewById(R.id.edt_company);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_tel);
        edt_town = findViewById(R.id.edt_town);

        btn_save = findViewById(R.id.btn_saveclient);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String str_edt_name = edt_name.getText().toString();
                String str_edt_address = edt_address.getText().toString();
                String str_edt_company = edt_company.getText().toString();
                String str_edt_email = edt_email.getText().toString();
                String str_edt_phone = edt_phone.getText().toString();
                String str_edt_town = edt_town.getText().toString();

                if (str_edt_name.isEmpty()) {
                    Toast.makeText(((AddClientActivity) context), "Saisir le nom du client.", Toast.LENGTH_SHORT).show();
                    return;
                }


                Client client = new Client();
                client.setName(str_edt_name);
                client.setPhone(str_edt_phone);
                client.setTown(str_edt_town);
                client.setEmail(str_edt_email);
                client.setCompany(str_edt_company);
                client.setAddress(str_edt_address);

                boolean createSuccessful = new TableControllerClient(context).create(client);
                if (createSuccessful) {
                    Toast.makeText(context, "Client été ajouté avec succée.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(((AddClientActivity) context), MainActivity.class);
                    startActivity(intent);

                }
            }

        });
    }
}
