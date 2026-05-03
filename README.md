# 🚅 Japan IC Reader (日本交通ICカードリーダー)

<p align="center">
  <img src="https://raw.githubusercontent.com/zits93/SuicaNFCReader/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" width="128" height="128" alt="App Icon">
</p>

**Japan IC Reader** is a premium, high-performance Android application designed for travelers and residents in Japan. It allows you to instantly read and visualize data from various Japanese transit IC cards (Suica, PASMO, ICOCA, and more) with a stunning modern interface and AI-powered translation.

---

## ✨ Key Features

- **🌐 AI-Powered Translation**: Utilizes Google ML Kit's on-device NMT (Neural Machine Translation) to translate thousands of Japanese station and line names into your language (English, Korean, Chinese) in real-time.
- **💎 Premium Design**: A state-of-the-art UI featuring liquid mesh gradients, glassmorphism effects, and smooth animations.
- **💳 Universal Support**: Works with all major FeliCa-based IC cards including Suica, PASMO, ICOCA, TOICA, SUGOCA, and more.
- **📊 Detailed History**: View your recent transaction history, including boarding/alighting stations, dates, and balance.
- **📳 Haptic Feedback**: Instant physical feedback upon successful card scans for a seamless user experience.
- **🌍 Multi-language UI**: Full localization for English, Korean, Japanese, and Chinese.

---

## 📸 Preview

<p align="center">
  <img src="https://raw.githubusercontent.com/zits93/SuicaNFCReader/main/docs/preview_main.png" width="300" alt="Main Screen">
  <img src="https://raw.githubusercontent.com/zits93/SuicaNFCReader/main/docs/preview_history.png" width="300" alt="History Screen">
</p>

---

## 🛠 Tech Stack

- **UI**: Jetpack Compose (Modern Declarative UI)
- **AI**: Google ML Kit (On-device Translation)
- **NFC**: Android NFC (FeliCa / NFC-F)
- **Architecture**: MVVM with LiveData & Coroutines
- **Build**: Kotlin DSL (build.gradle.kts) & Version Catalog

---

## 📥 Installation

1. Download the latest APK from the [Releases](https://github.com/zits93/SuicaNFCReader/releases) page.
2. Install the APK on your NFC-capable Android device.
3. Open the app and tap the "Download" button on the translation banner to setup the offline AI kit.
4. Touch your IC card to the back of your phone!

---

## 🤝 Contributing

Contributions are always welcome! Feel free to open an issue or submit a pull request if you have any ideas or bug fixes.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 🙏 Acknowledgments

This project originally started as a fork of [l0rded/SuicaNFCReader](https://github.com/l0rded/SuicaNFCReader). While the UI and core logic have been completely rewritten to support AI translation and modern design standards, we are deeply grateful to the original project for providing the foundational NFC communication logic and inspiration.

---

## 📄 License

This project is licensed under the MIT License.
