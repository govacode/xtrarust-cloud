# 设计模式

## 设计原则

1. 开闭原则
2. 里氏替换原则
3. 依赖倒置原则
4. 接口隔离原则
5. 迪米特法则
6. 合成复用原则

## 设计模式

### 创建型
1. 单例模式 singleton
2. 工厂模式 factory
- 简单工厂模式 simple factory
- 工厂方法模式 factory method
- 抽象工厂模式 abstract factory
3. 原型模式 prototype
4. 建造者模式 builder

### 结构型

> 描述如何将类或对象按某种布局组成更大的结构

1. 代理模式 proxy
2. 适配器模式 adapter
3. 装饰者模式 decorator
4. 桥接模式 bridge
5. 外观模式 facade
6. 组合模式 composite
7. 享元模式 flyweight

### 行为型

> 描述程序在运行时复杂的流程控制，即描述多个类或对象之间怎样相互协作共同完成单个对象都无法单独完成的任务，它涉及算法与对象间职责的分配。
> 
> 行为型模式分为类行为模式和对象行为模式，前者采用继承机制来在类间分派行为，后者采用组合或聚合在对象间分配行为。由于组合关系或聚合关系比继承关系耦合度低，满足“合成复用原则”，所以对象行为模式比类行为模式具有更大的灵活性。

1. 模板方法模式 template method
2. 策略模式 strategy
3. 命令模式 command
4. 责任链模式 chain of responsibility
5. 状态模式 state
6. 观察者模式 observer
7. 中介者模式 mediator
8. 迭代器模式 iterator
9. 访问者模式 visitor
10. 备忘录模式 memento
11. 解释器模式 interpreter

> 以上 11 种行为型模式，除了模板方法模式和解释器模式是类行为型模式，其他的全部属于对象行为型模式。

