# Nail pHolish App

## Overview
The Nail pHolish app is designed to work in conjunction with the Nail pHolish biosensor, a colorimetric nail polish that detects pH changes through interactions with body and environmental fluids. Using a Nix Color Sensor (Nix Spectro 2), the app converts the color of the nail polish into pH levels through RGB analysis, providing users with real-time chemical detection information. The app is intuitive and user-friendly, featuring three main pages: Intro, Dashboard, and Analyze.

## Features
- **Biosensor Nail Polish Detection**: 
   - Nail polish made with anthocyanins detects pH changes (e.g., pH 2, 4, 6, 8, 10).
   - Captures color changes on the nail and processes RGB data via the Nix Spectro 2 color sensor.
   
- **pH-Level Detection System**:
   - Uses the Nix Spectro 2 to capture RGB values from the nail surface.
   - Converts RGB values into pH levels using colorimetric analysis.
   - Euclidean distance calculation for accurate pH estimation.

## Pages

### 1. Intro Screen
The is the first screen the user is direct to when launching the app. The screen serves as a way for the user to have a layer of privacy, such that their information is not loaded as soon as the app launches. From this page, users can:
- Press "Begin" to continue to the Analyze screen.
<img src="https://github.com/jinhoyon/Nail-pHolish-App/blob/main/IntroScreen.png" alt="Intro Screen" width="400"/>

### 2. Analyze Screen
This page allows users to interact directly with the Nix Color Sensor (Nix Spectro 2). Key functionalities include:
- **Sensor Pairing**: Establish a Bluetooth connection with the Nix Color Sensor.
- **Color Measurement**: Capture RGB values from the biosensor nail polish on the user's fingernails.
- **Real-time pH Display**: Shows the pH level detected from the nail polish.
- **Add Measured Values**: Add recorded pH values to their respective category (saliva or beverage).
<img src="https://github.com/jinhoyon/Nail-pHolish-App/blob/main/AnalyzeScreen.png" alt="Analyze Screen" width="400"/>

### 3. Dashboard Screen
The Results page displays the processed data after RGB color capture, showing the estimated pH level based on the biosensor’s color change. The page features:
- **Graphical Visualization**: Provides a visual representation of the pH change over time or after different measurements.
<img src="https://github.com/jinhoyon/Nail-pHolish-App/blob/main/DashboardScreen.png" alt="Dashboard Screen" width="400"/>

## Setup and Installation

### Hardware Requirements:
- Nix Color Sensor (Nix Spectro 2)
- Android/iOS mobile device

## License
The Nail pHolish app is open-source and distributed under the MIT License. For more information, please refer to the LICENSE file in the repository.




