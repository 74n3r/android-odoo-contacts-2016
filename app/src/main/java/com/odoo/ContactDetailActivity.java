package com.odoo;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Data;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.orm.ListRow;
import com.odoo.table.ResCountry;
import com.odoo.table.ResPartner;
import com.odoo.table.ResState;
import com.odoo.utils.BitmapUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ResPartner resPartner;
    private ResState resState;
    private ResCountry resCountry;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fabEdit;
    private TextView textMobileNumber, textPhoneNumber, textEmail, textStreet, textStreet2,
            textCity, textState, textCountry, textPincode, textWebsite, textFax;
    private ImageView profileImage, callImage;
    private LinearLayout contactNumberLayout, emailLayout, addressLayout, websiteLayout, faxLayout;
    private RelativeLayout mobileLayout, phoneLayout;
    private LinearLayout editAddressLayout, editContactLayout, editEmailLayout, editWebsiteLayout,
            editFaxLayout;
    private EditText editMobileNumber, editPhoneNumber, editCity, editEmail, editState, editCountry,
            editPincode, editWebsite, editFax, editStreet, editStreet2;
    private String stringName, stringMobileNumber, stringPhoneNumber, stringEmail, stringStreet, stringStreet2,
            stringCity, stringPincode, stringStateId, stringStateName, stringCountryId,
            stringCountryName, stringWebsite, stringFax, stringImage;
    private int _id;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail_activity);

        toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.profile_collapsing);

        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(this);

        resState = new ResState(this);
        resCountry = new ResCountry(this);

        init();

        _id = getIntent().getIntExtra("id", 0);
        resPartner = new ResPartner(this);
        List<ListRow> rows = resPartner.select("_id = ?", String.valueOf(_id));
        if (rows != null) {
            for (ListRow row : rows) {

                stringName = row.getString("name");
                stringMobileNumber = row.getString("mobile");
                stringPhoneNumber = row.getString("phone");
                stringEmail = row.getString("email");
                stringStreet = row.getString("street");
                stringStreet2 = row.getString("street2");
                stringCity = row.getString("city");
                stringPincode = row.getString("zip");
                stringStateId = row.getString("state_id");
                stringCountryId = row.getString("country_id");
                stringFax = row.getString("fax");
                stringWebsite = row.getString("website");
                stringImage = row.getString("image_medium");

                //TODO: state_name and Country_name from id
                stringStateName = "false";
                stringCountryName = "false";


                collapsingToolbarLayout.setTitle(row.getString("name"));
                //contact number
                textMobileNumber.setText(stringMobileNumber);
                if (stringMobileNumber.equals("false") && !stringPhoneNumber.equals("false")) {
                    mobileLayout.setVisibility(View.GONE);
                    callImage.setVisibility(View.VISIBLE);
                }

                if (stringMobileNumber.equals("false")) {
                    mobileLayout.setVisibility(View.GONE);
                }

                textPhoneNumber.setText(stringPhoneNumber);
                if (stringPhoneNumber.equals("false")) {
                    phoneLayout.setVisibility(View.GONE);
                }

                if (stringMobileNumber.equals("false") && stringPhoneNumber.equals("false")) {
                    contactNumberLayout.setVisibility(View.GONE);
                }

                //email
                textEmail.setText(stringEmail);
                if (stringEmail.equals("false")) {
                    emailLayout.setVisibility(View.GONE);
                }

                //address
                textStreet.setText(stringStreet);
                textStreet.setVisibility(stringStreet.equals("false") ? View.GONE : View.VISIBLE);

                textStreet2.setText(stringStreet2);
                textStreet2.setVisibility(stringStreet2.equals("false") ? View.GONE : View.VISIBLE);

                textCity.setText(stringCity);
                textCity.setVisibility(stringCity.equals("false") ? View.GONE : View.VISIBLE);

                textPincode.setText(stringPincode);
                textPincode.setVisibility(stringPincode.equals("false") ? View.GONE : View.VISIBLE);

                textState.setText(stringStateId);
                textState.setVisibility(stringStateId.equals("0") ? View.GONE : View.VISIBLE);

                textCountry.setText(stringCountryId);
                textCountry.setVisibility(stringCountryId.equals("0") ? View.GONE : View.VISIBLE);

                if (stringStreet.equals("false") && stringStreet2.equals("false") &&
                        stringCity.equals("false") && stringPincode.equals("false") &&
                        stringStateId.equals("0") && stringCountryId.equals("0")) {
                    addressLayout.setVisibility(View.GONE);
                }

                //website
                textWebsite.setText(stringWebsite);
                if (stringWebsite.equals("false")) {
                    websiteLayout.setVisibility(View.GONE);
                }

                //fax
                textFax.setText(stringFax);
                if (stringFax.equals("false")) {
                    faxLayout.setVisibility(View.GONE);
                }

                //profile image
                if (!stringImage.equals("false")) {
                    profileImage.setImageBitmap(BitmapUtils.getBitmapImage(this, stringImage));
                } else {
                    profileImage.setImageBitmap(BitmapUtils.getAlphabetImage(this,
                            row.getString("name")));
                }

            }
        }
    }

    private void init() {

        textMobileNumber = (TextView) findViewById(R.id.textMobileNumber);
        textPhoneNumber = (TextView) findViewById(R.id.textPhoneNumber);
        textEmail = (TextView) findViewById(R.id.textEmail);
        textCity = (TextView) findViewById(R.id.textCity);
        textStreet = (TextView) findViewById(R.id.textStreet);
        textStreet2 = (TextView) findViewById(R.id.textStreet2);
        textState = (TextView) findViewById(R.id.textState);
        textCountry = (TextView) findViewById(R.id.textCountry);
        textWebsite = (TextView) findViewById(R.id.textWebsite);
        textFax = (TextView) findViewById(R.id.textFax);
        textPincode = (TextView) findViewById(R.id.textPincode);

        profileImage = (ImageView) findViewById(R.id.avatar);
        callImage = (ImageView) findViewById(R.id.imageCall2);

        contactNumberLayout = (LinearLayout) findViewById(R.id.contactNumberLayout);
        emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        websiteLayout = (LinearLayout) findViewById(R.id.websiteLayout);
        faxLayout = (LinearLayout) findViewById(R.id.faxLayout);

        mobileLayout = (RelativeLayout) findViewById(R.id.mobileLayout);
        phoneLayout = (RelativeLayout) findViewById(R.id.phoneLayout);

        editAddressLayout = (LinearLayout) findViewById(R.id.editAddressLayout);
        editContactLayout = (LinearLayout) findViewById(R.id.editContactLayout);
        editEmailLayout = (LinearLayout) findViewById(R.id.editEmailLayout);
        editWebsiteLayout = (LinearLayout) findViewById(R.id.editWebsiteLayout);
        editFaxLayout = (LinearLayout) findViewById(R.id.editFaxLayout);

        editMobileNumber = (EditText) findViewById(R.id.editMobileNumber);
        editPhoneNumber = (EditText) findViewById(R.id.editPhoneNumber);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editStreet = (EditText) findViewById(R.id.editStreet);
        editStreet2 = (EditText) findViewById(R.id.editStreet2);
        editCity = (EditText) findViewById(R.id.editCity);
        editState = (EditText) findViewById(R.id.editState);
        editCountry = (EditText) findViewById(R.id.editCountry);
        editPincode = (EditText) findViewById(R.id.editPincode);
        editWebsite = (EditText) findViewById(R.id.editWebsite);
        editFax = (EditText) findViewById(R.id.editFax);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fabEdit) {

            fabEdit.setImageResource(R.drawable.ic_done_24dp);
            editAddressLayout.setVisibility(View.VISIBLE);
            editContactLayout.setVisibility(View.VISIBLE);
            editEmailLayout.setVisibility(View.VISIBLE);
            editWebsiteLayout.setVisibility(View.VISIBLE);
            editFaxLayout.setVisibility(View.VISIBLE);

            addressLayout.setVisibility(View.GONE);
            contactNumberLayout.setVisibility(View.GONE);
            emailLayout.setVisibility(View.GONE);
            websiteLayout.setVisibility(View.GONE);
            faxLayout.setVisibility(View.GONE);

            editMobileNumber.setText(stringMobileNumber.equals("false") ? "" : stringMobileNumber);
            editPhoneNumber.setText(stringPhoneNumber.equals("false") ? "" : stringPhoneNumber);
            editEmail.setText(stringEmail.equals("false") ? "" : stringEmail);
            editCity.setText(stringCity.equals("false") ? "" : stringCity);
            editStreet.setText(stringStreet.equals("false") ? "" : stringStreet);
            editStreet2.setText(stringStreet2.equals("false") ? "" : stringStreet2);
            editState.setText(stringStateId.equals("0") ? "" : stringStateId);
            editCountry.setText(stringCountryId.equals("0") ? "" : stringCountryId);
            editWebsite.setText(stringWebsite.equals("false") ? "" : stringWebsite);
            editFax.setText(stringFax.equals("false") ? "" : stringFax);
            editPincode.setText(stringPincode.equals("false") ? "" : stringPincode);

            profileImage.setClickable(true);
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            fabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateRecords();
                    finish();
                }
            });
        }
    }

    private void updateRecords() {

        //TODO : FIX = sometime listview not update when updating records

        ContentValues values = new ContentValues();

        if (editMobileNumber.getText().toString().equals("")) {
            values.put("mobile", "false");
        } else {
            values.put("mobile", editMobileNumber.getText().toString());
        }

        if (editPhoneNumber.getText().toString().equals("")) {
            values.put("phone", "false");
        } else {
            values.put("phone", editPhoneNumber.getText().toString());
        }

        if (editCity.getText().toString().equals("")) {
            values.put("city", "false");
        } else {
            values.put("city", editCity.getText().toString());
        }

        if (editStreet.getText().toString().equals("")) {
            values.put("street", "false");
        } else {
            values.put("street", editStreet.getText().toString());
        }

        if (editStreet2.getText().toString().equals("")) {
            values.put("street2", "false");
        } else {
            values.put("street2", editStreet2.getText().toString());
        }

        if (editEmail.getText().toString().equals("")) {
            values.put("email", "false");
        } else {
            values.put("email", editEmail.getText().toString());
        }

        if (editWebsite.getText().toString().equals("")) {
            values.put("website", "false");
        } else {
            values.put("website", editWebsite.getText().toString());
        }

        if (editState.getText().toString().equals("")) {
            values.put("state_id", "0");
        } else {
            //TODO: state name
            values.put("state_id", "1");
        }

        if (editCountry.getText().toString().equals("")) {
            values.put("country_id", "0");
        } else {
            //TODO: Country name
            values.put("country_id", "1");
        }

        if (editFax.getText().toString().equals("")) {
            values.put("fax", "false");
        } else {
            values.put("fax", editFax.getText().toString());
        }

        if (editPincode.getText().toString().equals("")) {
            values.put("zip", "false");
        } else {
            values.put("zip", editPincode.getText().toString());
        }


        resPartner.update(values, "_id = ? ", String.valueOf(_id));
        Toast.makeText(ContactDetailActivity.this, "Contact Updated", Toast.LENGTH_SHORT).show();
    }

    private void selectImage() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        try {
            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent,
                    "Complete action using"), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = extras.getParcelable("data");
                    profileImage.setImageBitmap(bitmap);
                    stringImage = BitmapUtils.bitmapToBase64(bitmap);
                    ContentValues values = new ContentValues();
                    values.put("image_medium", stringImage);
                    resPartner.update(values, "_id = ? ", String.valueOf(_id));
                }
            } else {
                NavUtils.getParentActivityIntent(this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.menu_call) {

            if (stringMobileNumber.equals("false")) {
                if (stringPhoneNumber.equals("false")) {
                    Toast.makeText(this, "Number not found", Toast.LENGTH_LONG).show();
                } else {
                    callToContact(stringPhoneNumber);
                }
            } else {
                callToContact(stringMobileNumber);
            }
        }

        if (id == R.id.menu_add_contact_to_device) {

            stringImage = stringImage.equals("false") ? "" : stringImage;
            stringMobileNumber = stringMobileNumber.equals("false") ? "" : stringMobileNumber;
            stringPhoneNumber = stringPhoneNumber.equals("false") ? "" : stringPhoneNumber;
            stringEmail = stringEmail.equals("false") ? "" : stringEmail;
            stringStreet = stringStreet.equals("false") ? "" : stringStreet;
            stringStreet2 = stringStreet2.equals("false") ? "" : stringStreet2;
            stringCity = stringCity.equals("false") ? "" : stringCity;
            stringCountryName = stringCountryName.equals("false") ? "" : stringCountryName;
            stringWebsite = stringWebsite.equals("false") ? "" : stringWebsite;
            stringFax = stringFax.equals("false") ? "" : stringFax;
            stringPincode = stringPincode.equals("false") ? "" : stringPincode;

            addContactToDevice(stringName, stringImage, stringMobileNumber, stringPhoneNumber, stringEmail,
                    stringStreet, stringStreet2, stringCity, stringCountryName, stringFax,
                    stringWebsite, stringPincode);
        }

        if (id == R.id.menu_send_mail) {
            if (stringEmail.equals("false")) {
                Toast.makeText(this, "Email not found", Toast.LENGTH_LONG).show();
            } else {
                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setData(Uri.parse("mailto:"));
                mailIntent.setType("text/plain");
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{stringEmail});
                startActivity(mailIntent);
            }
        }

        if (id == R.id.menu_send_message) {
            if (stringMobileNumber.equals("false")) {
                if (stringPhoneNumber.equals("false")) {
                    Toast.makeText(this, "Number not found", Toast.LENGTH_LONG).show();
                } else {
                    sendMessage(stringPhoneNumber);
                }
            } else {
                sendMessage(stringMobileNumber);
            }
        }

        if (id == R.id.menu_delete) {
            resPartner.delete("_id = ? ", String.valueOf(_id));
            Toast.makeText(ContactDetailActivity.this, "Contact Deleted", Toast.LENGTH_LONG).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addContactToDevice(String stringName, String stringImage, String stringMobileNumber,
                                    String stringPhoneNumber, String stringEmail, String stringStreet,
                                    String stringStreet2, String stringCity, String stringCountryName,
                                    String stringFax, String stringWebsite, String stringPincode) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = 0;

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        // Display name
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        stringName)
                .build());

        // avatar
        if (!stringImage.equals("false") && !stringImage.isEmpty()) {
            Bitmap bitmap = BitmapUtils.getBitmapImage(this, stringImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                            baos.toByteArray()).build());
        }

        // Mobile number
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, stringMobileNumber)
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build());

        // Phone number
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, stringPhoneNumber)
                .withValue(Phone.TYPE, Phone.TYPE_HOME)
                .build());

        // Fax number
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, stringFax)
                .withValue(Phone.TYPE, Phone.TYPE_OTHER_FAX)
                .build());

        // Email
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, stringEmail)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK).build());

        // Website
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Website.URL, stringWebsite)
                .withValue(ContactsContract.CommonDataKinds.Website.TYPE,
                        ContactsContract.CommonDataKinds.Website.TYPE_HOME).build());

        // address, city, zip, county

        address = String.valueOf(new StringBuilder(stringStreet).append(", ").append(stringStreet2));
        if (stringStreet.equals("")) {
            if (stringStreet2.equals("")) {
                address = "";
            } else {
                address = stringStreet2;
            }
        } else {
            address = stringStreet;
        }

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address)
                .withValue(Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        stringCity)
                .withValue(Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                        stringPincode)
                //TODO : country name
                /*.withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                        stringCountryName)*/
                .build());

        try {
            ContentProviderResult[] res = this.getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
    }


    public void sendMessage(String number) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", number);
        smsIntent.putExtra("sms_body", "");
        startActivity(smsIntent);
    }

    public void callToContact(String number) {
        Uri phoneCall;
        Intent dial;
        phoneCall = Uri.parse("tel:" + number);
        dial = new Intent(Intent.ACTION_CALL, phoneCall);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactDetailActivity.this, "Call Permission required", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(dial);
        }
    }
}
