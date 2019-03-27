package com.petworld_madebysocialworld;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

public class DrawerUtil {
    public static void getDrawer(final Activity activity, Toolbar toolbar, GoogleSignInAccount account) {

        //info account
        String personName = account.getDisplayName();
        String personGivenName = account.getGivenName();
        String personFamilyName = account.getFamilyName();
        String personEmail = account.getEmail();
        String personId = account.getId();
        Uri personPhoto = account.getPhotoUrl();

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(personName).withEmail(personEmail).withIcon(personPhoto))
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
                        drawerItemSettings
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1 && !(activity instanceof UserActivity)) {
                            // load tournament screen
                            Intent intent = new Intent(activity, UserActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 5 && !(activity instanceof MapActivity)) {
                            // load tournament screen
                            Intent intent = new Intent(activity, MapActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    }
                })
                .build();
    }
}

