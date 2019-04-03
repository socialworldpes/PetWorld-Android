package com.petworld_madebysocialworld;

import Models.User;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

public class DrawerUtil {
    public static void getDrawer(final Activity activity, Toolbar toolbar) {

        GoogleSignInAccount account = User.getInstance().getAccount();
        //info account
        String personName = account.getDisplayName();
        String personGivenName = account.getGivenName();
        String personFamilyName = account.getFamilyName();
        String personEmail = account.getEmail();
        String personId = account.getId();
        Uri personPhoto = account.getPhotoUrl();
        Log.d("Prueba", personPhoto.getPath());

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
        ExpandableDrawerItem drawerItemManagePets = new ExpandableDrawerItem()
                .withIdentifier(2).withName("Mascotas").withIcon(R.drawable.ic_pets).withSelectable(false).withSubItems(
                new SecondaryDrawerItem().withName("Mascota 1").withLevel(2).withIdentifier(2001),
                new SecondaryDrawerItem().withName("Mascota 2").withLevel(2).withIdentifier(2002)
        );;


        SecondaryDrawerItem drawerItemAddPet = new SecondaryDrawerItem().withIdentifier(3)
                .withName("Añadir mascota").withIcon(R.drawable.ic_add);
        SecondaryDrawerItem drawerItemGroups = new SecondaryDrawerItem().withIdentifier(4)
                .withName("Grupos").withIcon(R.drawable.ic_group);
        SecondaryDrawerItem drawerItemRoutes = new SecondaryDrawerItem().withIdentifier(5)
                .withName("Tus rutas").withIcon(R.drawable.ic_rutas);
        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(6)
                .withName("Ajustes").withIcon(R.drawable.ic_settings);
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
                        drawerItemSettings,
                        drawerItemLogOut
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1 && !(activity instanceof UserActivity)) {
                            Intent intent = new Intent(activity, UserActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 5 && !(activity instanceof MapActivity)) {
                            Intent intent = new Intent(activity, MapActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 2001 && !(activity instanceof PetProfileActivity)) {
                            Intent intent = new Intent(activity, PetProfileActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 7){
                            //to improve
                            MainActivity aux = new MainActivity();
                            User.getInstance().setLogOut(true);
                            Intent intent = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    }
                })
                .build();
    }
}
