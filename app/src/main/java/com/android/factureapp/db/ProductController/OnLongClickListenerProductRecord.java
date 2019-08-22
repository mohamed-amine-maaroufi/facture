package com.android.factureapp.db.ProductController;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.android.factureapp.MainActivity;


/*
*
* this class responsable on the clique of records
* To delete record
 */
public class OnLongClickListenerProductRecord implements View.OnLongClickListener {

    Context context;
    String id;

    @Override
    public boolean onLongClick(View view) {

        context = view.getContext();
        id = view.getTag().toString();

        final CharSequence[] items = { "Delete" };

        new AlertDialog.Builder(context).setTitle("Action de produit")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {

                            boolean deleteSuccessful = new TableControllerProduct(context).delete(Integer.parseInt(id));

                            if (deleteSuccessful){
                                Toast.makeText(context, "Produit été supprimé avec succée", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Unable to delete product record.", Toast.LENGTH_SHORT).show();
                            }

                            ((MainActivity) context).countRecords();
                            ((MainActivity) context).readRecords();

                            if(((MainActivity) context).recordCount == 0){
                                ((MainActivity) context).edt_tva.setVisibility(View.GONE);
                                ((MainActivity) context).btn_generateFacture.setEnabled(false);

                            }

                        }
                        dialog.dismiss();

                    }
                }).show();
        return false;
    }

}