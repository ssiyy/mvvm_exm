import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_webview_plugin/flutter_webview_plugin.dart';

class WebView extends StatefulWidget {
  @override
  _WebViewState createState() => _WebViewState()
}

class _WebViewState extends State<WebView> {
  final webViewReference = FlutterWebviewPlugin();
  StreamSubscription<String> _onUrlChanged;

  StreamSubscription<WebViewStateChanged> _onStateChange;

  StreamSubscription<WebViewHttpError> _onHttpError;

  @override
  void initState() {
    super.initState();
    webViewReference.close();
    _onUrlChanged = webViewReference.onUrlChanged.listen((String url) {

    });
    _onStateChange = webViewReference.onStateChanged.listen((
        WebViewStateChanged stateChange) {
      switch (stateChange.type) {
        case WebViewState.startLoad:
          break;
        default:
      }
    });
    _onHttpError =
        webViewReference.onHttpError.listen((WebViewHttpError error) {

        });
  }

  @override
  void dispose() {
    _onStateChange.cancel();
    _onUrlChanged.cancel();
    _onHttpError.cancel();
    webViewReference.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: <Widget>[
          _appBar(),
        ],
      ),
    );
  }

  Widget _appBar() {
    return Container(
      child: FractionallySizedBox(
        widthFactor: 1,
        child: Stack(
          children: <Widget>[
            GestureDetector(
              child: Container(
                margin: EdgeInsets.only(left: 10),
                child: Icon(
                    Icons.backspace,
                    color: Color(0xffffff),
                    size: 26
                ),
              ),
            ),
            Positioned(
                left: 0,
                right: 0, child: Center(
              child: Text("这里是标题",
                  style: TextStyle(color: Color(0xfefefe), fontSize: 26)),
            ))
          ],
        ),
      ),
    );
  }

}