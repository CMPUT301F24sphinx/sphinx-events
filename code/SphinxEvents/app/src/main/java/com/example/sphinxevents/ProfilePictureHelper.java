/*
 * Class Name: ProfilePictureHelper
 * Date: 2024-12-01
 *
 * Description:
 * A utility class for managing profile pictures, including generating default profile pictures,
 * saving and retrieving Bitmaps, and uploading them to Firebase Storage.
 *
 */

package com.example.sphinxevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A utility class for managing profile pictures, including generating default profile pictures,
 * saving and retrieving Bitmaps, and uploading them to Firebase Storage.
 */
public class ProfilePictureHelper {

    /**
     * Generates a deterministic default profile picture for the user and uploads it to Firebase Storage.
     * @param context The application context.
     * @param user The current user for whom the profile picture is being generated.
     * @param callback The callback for success or failure of the upload.
     */
    public static void generateAndUploadDefaultProfilePicture(Context context, Entrant user, DatabaseManager.UploadProfilePictureCallback callback) {
        // Generate the default profile picture (deterministic)
        Drawable defaultPfp = generateDefaultProfilePicture(context, user);

        // Convert drawable to bitmap
        Bitmap bitmap = drawableToBitmap(defaultPfp, 140, 140); // Adjust size if necessary

        // Save the bitmap to a temporary file and get its URI

        Uri profilePictureUri = saveBitmapToTempFile(context, bitmap);
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.uploadProfilePicture(user.getDeviceId(), profilePictureUri, new DatabaseManager.UploadProfilePictureCallback() {
            @Override
            public void onSuccess(String url) {
                user.setProfilePictureUrl(url);
                user.setCustomPfp(false);
                dbManager.saveUser(user, new DatabaseManager.UserCreationCallback() {
                    @Override
                    public void onSuccess(String deviceId) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Error generating new profile picture", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Error generating new profile picture", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Generates a default deterministic profile picture based on the user's first name character.
     * @param context The application context.
     * @param user The current user for whom the profile picture is being generated.
     * @return The generated drawable profile picture.
     */
    public static Drawable generateDefaultProfilePicture(Context context, Entrant user) {
        return TextDrawable.createTextDrawable(
                context,
                String.valueOf(user.getName().charAt(0)),
                Color.WHITE,
                140 // Profile picture size
        );
    }

    /**
     * Converts a Drawable object to a Bitmap.
     * @param drawable The Drawable to convert.
     * @param width The width of the resulting Bitmap.
     * @param height The height of the resulting Bitmap.
     * @return The converted Bitmap.
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Saves a Bitmap to a file and returns the URI of the saved file.
     * @param context The application context.
     * @param bitmap The bitmap to save.
     * @param fileName The name of the file to save the bitmap as.
     * @return The URI pointing to the saved file.
     * @throws IOException If an I/O error occurs while saving the file.
     */
    public static Uri saveBitmapToFile(Context context, Bitmap bitmap, String fileName) throws IOException {
        // Create a file to store the image
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();  // Create the directory if it doesn't exist
        }

        File file = new File(storageDir, fileName);
        FileOutputStream outStream = new FileOutputStream(file);

        // Compress the bitmap to the file
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        outStream.flush();
        outStream.close();

        // Return the URI to the file
        return Uri.fromFile(file);
    }

    /**
     * Saves a Bitmap to a temporary file in the app's cache directory.
     *
     * @param context The application context.
     * @param bitmap The Bitmap to save.
     * @return The URI of the saved temporary file, or null if an error occurs.
     */
    public static Uri saveBitmapToTempFile(Context context, Bitmap bitmap) {
        // Use a fixed file name to always overwrite the same file
        String fileName = "temp_image.png";

        // Create a file in the app's internal storage directory (cache directory)
        File tempFile = new File(context.getCacheDir(), fileName);

        // Attempt to write the bitmap to the file
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            // Compress the Bitmap into the file as a PNG (you can change the format if needed)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            // Return the URI of the overwritten (or newly created) temporary file
            return Uri.fromFile(tempFile);
        } catch (IOException e) {
            // Log the exception or handle the error as needed
            e.printStackTrace();  // You can log this to Logcat or show a toast
            return null;  // Return null or handle the error in another way
        }
    }

    /**
     * Retrieves a Bitmap from a file URI.
     *
     * @param uri The URI pointing to the image file.
     * @return The decoded Bitmap, or null if the file cannot be found or read.
     */
    public static Bitmap getBitmapFromUri(Uri uri) {
        try {
            // Open the file corresponding to the URI and decode it into a Bitmap
            InputStream inputStream = new FileInputStream(uri.getPath());
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null; // Return null if the file cannot be found or read
        }
    }

}


