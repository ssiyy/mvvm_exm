import 'package:json_annotation/json_annotation.dart';


part "tag.g.dart";

@JsonSerializable()
class Tag{
  final String name;//":"问答",
  final String url;//":"/article/list/0?cid=440"

  Tag(this.name, this.url);

  factory Tag.fromJson(Map<String,dynamic> json) => _$TagFromJson(json);

  Map<String,dynamic> toJson()=>_$TagToJson(this);

}