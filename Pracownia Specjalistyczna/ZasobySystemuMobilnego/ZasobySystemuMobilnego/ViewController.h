//
//  ViewController.h
//  ZasobySystemuMobilnego
//
//  Created by Mateusz_ on 13/12/2023.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController

@property (weak, nonatomic) IBOutlet UILabel *gestureLabel;

- (IBAction) tapGesture: (UITapGestureRecognizer *) sender;
- (IBAction) pinchGesture: (UIPinchGestureRecognizer *) sender;
- (IBAction) swipeGesture: (UISwipeGestureRecognizer *) sender;
- (IBAction) longPressGesture: (UILongPressGestureRecognizer *) sender;

@end

