package com.petworld_madebysocialworld;

import Models.User;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrawerUtil {
    static int i;
    public static void getDrawer(final Activity activity, Toolbar toolbar) {

        i = 0;
        final HashMap<Integer, DocumentReference> mapPetRef =  new HashMap<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //info account
        String personName = user.getDisplayName();
        String personEmail = user.getEmail();
        Uri personPhoto = user.getPhotoUrl();
        final String userID = user.getUid();


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }
            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }
        });

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(personName).withEmail(personEmail).withIcon("https://"+personPhoto.getHost()+personPhoto.getPath()))
                .withSelectionListEnabledForSingleProfile(false)
                .withDividerBelowHeader(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();




        PrimaryDrawerItem drawerItemManageUser = new PrimaryDrawerItem().withIdentifier(1)
                .withName("Usuario").withIcon(R.drawable.ic_profile);
        final ExpandableDrawerItem drawerItemManagePets = new ExpandableDrawerItem()
                .withIdentifier(2).withName("Mascotas").withIcon(R.drawable.ic_pets).withSelectable(false);

        //pets menu lateral
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userID);
        Log.d("petMenu: ", "id doc user: " + docRef.toString());
        Log.d("petMenu: ", "id user: " + userID);

        //get current user document
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("task string", task.toString());
                    Log.d("petMenu pet document: ", "" + task.getResult().get("pets"));
                    DocumentSnapshot result = task.getResult();
                    ArrayList<DocumentReference> arrayPets = (ArrayList<DocumentReference>) result.get("pets");

                    //if user have pets
                    if (arrayPets != null) {
                        for (final DocumentReference dr : arrayPets) {
                            Log.d("petMenu: ", "interacion: " + i);
                            if (dr.getPath() !=  null) {
                                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot result = task.getResult();
                                        String namePet = (String) result.get("name");
                                        Long identifier = new Long(2001 + i);
                                        drawerItemManagePets.withSubItems(
                                                new SecondaryDrawerItem().withName(namePet).withLevel(2).withIdentifier(identifier)
                                        );
                                        Log.d("petMenu identifier: ", "" + identifier );
                                        mapPetRef.put(identifier.intValue(), dr);
                                        i++;
                                    }
                                });

                            }

                        }
                        //log mapPetRf
                        Log.d("mapPetRef : ", "antes del for: " + mapPetRef.size());
                        for (Map.Entry<Integer, DocumentReference> entry : mapPetRef.entrySet()) {
                            Log.d("mapPetRef : ", "id pet menu lateral: " + entry.getKey() + "path: " + entry.getValue());
                        }

                    }
                    i = 0;
                  /*  for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("task ok", document.getId() + " => " + document.getData());
                        Map<String, Object> aux = task.getResult().getDocuments().get(i).getData();
                        drawerItemManagePets.withSubItems(
                                new SecondaryDrawerItem().withName("" + aux.get("name")).withLevel(2).withIdentifier(2001 + i)
                        );
                        i++;
                    }
                    */
                }
            }
        });




        SecondaryDrawerItem drawerItemAddPet = new SecondaryDrawerItem().withIdentifier(3)
                .withName("Añadir mascota").withIcon(R.drawable.ic_add);
        SecondaryDrawerItem drawerItemGroups = new SecondaryDrawerItem().withIdentifier(4)
                .withName("Amigos").withIcon(R.drawable.ic_group);
        SecondaryDrawerItem drawerItemRoutes = new SecondaryDrawerItem().withIdentifier(5)
                .withName("Tus rutas").withIcon(R.drawable.ic_rutas);
        SecondaryDrawerItem drawerItemLogOut = new SecondaryDrawerItem().withIdentifier(7)
                .withName("Cerrar sesión").withIcon(R.drawable.ic_logout);


        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withHeaderPadding(false)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        drawerItemManageUser,
                        drawerItemManagePets,
                        new DividerDrawerItem(),
                        drawerItemAddPet,
                        drawerItemGroups,
                        drawerItemRoutes,
                        drawerItemLogOut
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1 && !(activity instanceof UserActivity)) {
                            Intent intent = new Intent(activity, UserActivity.class);
                            intent.putExtra("id", userID);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 5 && !(activity instanceof MapActivity)) {
                            Intent intent = new Intent(activity, MapActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        //lista de mascotas
                        boolean dentroIf = false;
                        for (Map.Entry<Integer, DocumentReference> entry : mapPetRef.entrySet()) {
                            if (drawerItem.getIdentifier() == entry.getKey() && !(activity instanceof ViewPetActivity)) {

                                if (true) {
                                    Intent  intent = new Intent(activity, ViewPetActivity.class);
                                    String petPath = entry.getValue().getPath();
                                    Log.d("drawerPetRef", petPath);
                                    intent.putExtra("docPetRef", petPath);
                                    view.getContext().startActivity(intent);
                                }

                                dentroIf = true;
                            }
                        }
                        if (drawerItem.getIdentifier() == 7 ){
                            //to improve
                            MainActivity aux = new MainActivity();
                            User.getInstance().setLogOut(true);
                            Intent intent = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(intent);
                        }

                        if (drawerItem.getIdentifier() == 3 && !(activity instanceof CreatePetActivity)){
                            Intent intent = new Intent(activity, CreatePetActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 4 && !(activity instanceof FriendsActivity)){
                            Intent intent = new Intent(activity, FriendsActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    }
                })
                .build();
    }


}
