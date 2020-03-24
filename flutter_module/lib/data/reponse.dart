import 'package:json_annotation/json_annotation.dart';

part 'reponse.g.dart';

@JsonSerializable()
class Reponse {
  @JsonKey(name: "errorCode")
  final int errorCode;

  @JsonKey(name: "errorMsg")
  final String errorMsg;

  @JsonKey(name: "data")
  final dynamic data;

  Reponse(this.errorCode, this.errorMsg, this.data);

  get isSuccess => errorCode == 0;

  factory Reponse.fromJson(Map<String, dynamic> json) =>
      _$ReponseFromJson(json);

  Map<String, dynamic> toJson() => _$ReponseToJson(this);
}
