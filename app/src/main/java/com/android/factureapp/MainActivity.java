package com.android.factureapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.factureapp.beans.Client;
import com.android.factureapp.beans.Product;
import com.android.factureapp.db.ClientController.TableControllerClient;
import com.android.factureapp.db.ProductController.OnLongClickListenerProductRecord;
import com.android.factureapp.db.ProductController.TableControllerProduct;

import com.android.factureapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llScroll;
    private Bitmap bitmap;

    static Button btn_addproduct, btn_addclient;
    public Button btn_generateFacture;
    final Context context = this;
    EditText edt_productname;
    EditText edt_reference;
    EditText edt_unitprice;
    EditText edt_qantity;
    public EditText edt_tva;
    Button dialogButton;
    TextView textView_totalPrice;
    ImageView imageViewLogo;

    Spinner spinner_client;
    EditText edt_company, edt_town, edt_email, edt_phone, edt_address;

    List<Product> productList;

    int totalprice = 0;
    float totalPriceWithTVA;
    float TVA = 0;

    public static int recordCount;

    private String factureName;

    final int GALLERY_REQUEST_CODE = 120;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //nombre of products
        countRecords();
        //set products in user view
        readRecords();

        btn_addclient = findViewById(R.id.btn_addclient);
        btn_addclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddClientActivity.class);
                startActivity(intent);
            }
        });

        //pic image logo from gallery
        imageViewLogo = findViewById(R.id.imageViewLogo);
        imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickFromGallery();
            }
        });

        productList  = new TableControllerProduct(this).read();

        edt_tva = findViewById(R.id.edt_tva);
        textView_totalPrice = findViewById(R.id.text_totalprice);
        llScroll = findViewById(R.id.llScroll);
        btn_generateFacture = findViewById(R.id.btn_generateFacture);


        /* Set Text Watcher listener */
        edt_tva.addTextChangedListener(TotalPriceWatcher);


        if(recordCount > 0){
            edt_tva.setVisibility(View.VISIBLE);
            btn_generateFacture.setEnabled(true);
            btn_generateFacture.setBackground(getResources().getDrawable(R.drawable.shape_btn_vert));

        }else{
            edt_tva.setVisibility(View.GONE);
            btn_generateFacture.setEnabled(false);
            btn_generateFacture.setBackground(getResources().getDrawable(R.drawable.shape_btn_grey));

        }

        btn_addproduct = findViewById(R.id.btn_addproduct);
        btn_addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.addproduct);
                dialog.setTitle("Ajouter un produit");

                //biding the composants of dialogbox
                dialogButton = (Button) dialog.findViewById(R.id.saveproduct);
                edt_productname = (EditText) dialog.findViewById(R.id.edt_productname);
                edt_reference = (EditText) dialog.findViewById(R.id.edt_reference);
                edt_unitprice = (EditText) dialog.findViewById(R.id.edt_unitprice);
                edt_qantity = (EditText) dialog.findViewById(R.id.edt_qantity);




                // if button is clicked, add product - close dialogBox
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String str_productname = edt_productname.getText().toString();
                        String str_reference = edt_reference.getText().toString();
                        String str_unitprice = edt_unitprice.getText().toString();
                        String str_qantity = edt_qantity.getText().toString();

                        if(str_productname.isEmpty()){
                            Toast.makeText(((MainActivity) context),"Saisir le nom du produit.",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(str_unitprice.isEmpty()){
                            Toast.makeText(((MainActivity) context),"Saisir le prix unitaire.",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(str_qantity.isEmpty()){
                            Toast.makeText(((MainActivity) context),"Saisir la quantité.",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Product product = new Product();
                        product.setName(str_productname);
                        product.setReference(str_reference);
                        product.setQuantity(str_qantity);
                        product.setUnitPrice(str_unitprice);

                        boolean createSuccessful = new TableControllerProduct(context).create(product);
                        if(createSuccessful){
                            Toast.makeText(context, "Produit été ajouté avec succée.", Toast.LENGTH_SHORT).show();
                            ((MainActivity) context).readRecords();
                            //nombre of products
                            countRecords();
                            productList  = new TableControllerProduct((MainActivity) context).read();

                            //enable tva% and btn generate facture
                            if(btn_generateFacture.isEnabled() == false){
                                edt_tva.setVisibility(View.VISIBLE);
                                btn_generateFacture.setEnabled(true);
                                btn_generateFacture.setBackground(getResources().getDrawable(R.drawable.shape_btn_vert));
                            }

                            dialog.dismiss();
                        }else{
                            Toast.makeText(context, "Unable to save product information.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                dialog.show();
            }
        });


        btn_generateFacture = findViewById(R.id.btn_generateFacture);
        btn_generateFacture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("size"," "+llScroll.getWidth() +"  "+llScroll.getWidth());
                bitmap = loadBitmapFromView(llScroll, llScroll.getWidth(), llScroll.getHeight());
                createPdf();
            }
        });




        //select client in the facture
        edt_address = findViewById(R.id.edt_address);
        edt_company = findViewById(R.id.edt_company);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_tel);
        edt_town = findViewById(R.id.edt_town);

        spinner_client = findViewById(R.id.spinner_client);
        List<Client> spinnerArray =  new TableControllerClient(this).read();
        ArrayAdapter<Client> adapter = new ArrayAdapter<Client>(
                this, android.R.layout.simple_spinner_item, spinnerArray);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_client.setAdapter(adapter);

        spinner_client.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Client client = (Client) parent.getSelectedItem();
                displayUserData(client);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void getSelectedUser(View v) {
        Client client = (Client) spinner_client.getSelectedItem();
        displayUserData(client);
    }

    private void displayUserData(Client client) {

        edt_address.setText("Adresse : " + client.getAddress().toString());
        edt_company.setText("Entreprise : " + client.getCompany().toString());
        edt_phone.setText("Tél : " + client.getPhone().toString());
        edt_town.setText("Ville : " + client.getTown().toString());
        edt_email.setText("Email : " + client.getEmail().toString());

    }

    //methode pour faire le calcul automatiquement lorsque on met le TVA%
    private final TextWatcher TotalPriceWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textView_totalPrice.setVisibility(View.VISIBLE);
        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                textView_totalPrice.setVisibility(View.GONE);
            } else{



                for (int i = 0; i < productList.size(); i++) {
                    totalprice += Integer.parseInt(productList.get(i).getUnitPrice()) * Integer.parseInt(productList.get(i).getQuantity());
                }

                TVA = (totalprice / 100) * Float.parseFloat(edt_tva.getText().toString());
                totalPriceWithTVA = totalprice + TVA;
                textView_totalPrice.setText( String.valueOf(totalPriceWithTVA));
                totalPriceWithTVA = 0;
                TVA = 0;
                totalprice = 0;

            }
        }
    };


    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void createPdf(){

        recordCount = new TableControllerProduct(this).count();
        if(recordCount == 0){
            Toast.makeText(getApplicationContext(),"Il faudrait ajouter au moins un produit.", Toast.LENGTH_SHORT).show();

        }
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels  ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        factureName = getTimeStamp();
        String folder_main = getString(R.string.app_name);

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        // write the document content
        //String targetPdf =   "/sdcard/" + getString(R.string.app_name) + "/" + factureName + ".pdf";
        String targetPdf =   Environment.getExternalStorageDirectory() + "/"  +folder_main + "/" + factureName + ".pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "PDF of Scroll is created!!!", Toast.LENGTH_SHORT).show();

        new TableControllerProduct(context).deleteAll();
        Intent intent1 = new Intent(MainActivity.this,MainActivity.class);
        startActivity(intent1);
        openGeneratedPDF();

    }

    private void openGeneratedPDF(){
        //File file = new File("/sdcard/" + getString(R.string.app_name) + "/" + factureName +".pdf");
        String folder_main = getString(R.string.app_name);
        File file = new File(Environment.getExternalStorageDirectory() +"/"  + folder_main + "/" + factureName + ".pdf");
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {

                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(MainActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    //dispaly number of products in TextView
    public void countRecords() {
        recordCount = new TableControllerProduct(this).count();
        TextView textViewRecordCount = (TextView) findViewById(R.id.textViewRecordCount);
        textViewRecordCount.setText(recordCount + " produits.");

    }


    //display database records to user interface
    public void readRecords() {

        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);
        linearLayoutRecords.removeAllViews();

        List<Product> products = new TableControllerProduct(this).read();

        if (products.size() > 0) {

            for (Product obj : products) {

                int id = obj.getId();
                String name = obj.getName();
                String reference = obj.getReference();
                String quantity = obj.getQuantity();
                String unitPrice = obj.getUnitPrice();

                String textViewContents = "Produit : " + name + "\t \t \t \t- \t \t\t \t" +
                        "Réference : " + reference
                        + "\n" +
                        "Quantité : " + quantity + "\t \t \t \t - \t \t \t \t" +
                        "Prix unitaire : " + unitPrice
                        + "\n" + "____________________________________________________";

                TextView textViewProductItem= new TextView(this);
                textViewProductItem.setPadding(0, 12, 0, 12);
                textViewProductItem.setText(textViewContents);
                textViewProductItem.setTag(Integer.toString(id));

                //action of clique record
                textViewProductItem.setOnLongClickListener(new OnLongClickListenerProductRecord());

                linearLayoutRecords.addView(textViewProductItem);
            }

        }

        else {

            TextView locationItem = new TextView(this);
            locationItem.setPadding(8, 8, 8, 8);
            locationItem.setText("Pas de produit pour le moment.");

            linearLayoutRecords.addView(locationItem);
        }

    }


    private String getTimeStamp(){
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        return timeStamp;
    }



    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    imageViewLogo.setImageURI(selectedImage);
                    imageViewLogo.setBackgroundColor(Color.WHITE);
                    break;
            }
    }



}