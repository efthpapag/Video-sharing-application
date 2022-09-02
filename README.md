# Video sharing application

The purpose of this project is to create an an application through which the users can share videos that they have stored in their device or recorded through the application. the communication between the users is done via the brokers.

# Execution 

1. Open 2 emulators in android studio.
2. The videos to be published must be in the downloads folder of the emulator that will publish them. The video used for the test is
https://drive.google.com/file/d/1CAYtz_djO0CWJROXBopWbrhXXI-kyXxE/view?usp=sharing.
3. Using redir for the 2 emulators, the port of the host and the emulator must be the same.
4. Open the folder where the project package is located with the command cd [folder where the app is stored]\DSApp\app\src\main\java in 3 cmd.
5. Run the command javac ./gr/aueb/dsapp/BackEnd/*.java in one of the cmds.
6. Open the Brokers by running the command java gr.aueb.dsapp.BackEnd.Broker [ip] [port] in each of the cmd
eg java gr.aueb.dsapp.BackEnd.Broker 127.0.0.1 3001. To test the application, ip 127.0.0.1 was used for all Brokers
and ports 3001, 3002, 3003. If these ip and ports are not used, the appropriate changes must be made in the Brokers.txt file.

# Application usage

1. After starting the application via android studio, the fields Username and Port must be filled. In Username the only limitation is that each user
must have a unique username. The port must be the one used in the redir of the emulator running the application. Finally, the user presses login.
2. Publish :
    a) The user writes the hashtag he wants in #[hashtag] format, then clicks add so that
    the hastag added to those of the video. Repeat the process for all the hashtags.
    b) The user fills in the video name if the video is stored in his emulator. The video name must be the same as the name of the mp4 file without the .mp4.
    If the user wants to post a new video that will be shot at that moment then the name does not matter. In both cases the user must not have posted
    video with the same name before.
    c) If the video is already stored in the downloads folder of the emulator, then the user presses publish. If he wants to capture a new video, he presses capture video. As soon as
    the video is done being recored it will be automatically published and saved in downloads.

3. Subscribe:
    a) The user writes the name of the channel or hashtag whose videos he wants to find and presses subscribe.
    b) The user selects the video he wants to see from the list by pressing the play button to his right.
    c) If the user wants to save the video, he clicks the download option which is located under the video that is playing. The video is saved in the user's download.
4. Remove : The user selects the video he wants to delete from the videos he published by pressing the corresponding delete button.
5. Close : closes the application and informs the brokers about the disconnection of the user.