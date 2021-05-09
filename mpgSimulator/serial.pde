List<Integer> createBytes(){  
  List<Integer> data = Stream.of(
    Stream.of(30,30,2+1+servos.size()*2),
    servos.stream()
    .map(s->s.getPWM())
    .flatMap(i -> Stream.of((i>>8)&0xFF,i&0xFF))
  ).flatMap(s->s).collect(Collectors.toList());
  
  return Stream.of(data.stream(), Stream.of(data.stream().reduce(0,(a,b)->a^b))).flatMap(s->s).collect(Collectors.toList());
}
